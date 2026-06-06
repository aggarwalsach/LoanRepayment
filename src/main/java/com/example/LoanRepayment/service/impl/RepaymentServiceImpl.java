package com.example.LoanRepayment.service.impl;

import com.example.LoanRepayment.dto.RepaymentRequest;
import com.example.LoanRepayment.dto.RepaymentResponse;
import com.example.LoanRepayment.dto.RepaymentReversalResponse;
import com.example.LoanRepayment.entity.*;
import com.example.LoanRepayment.enums.ChargeStatus;
import com.example.LoanRepayment.enums.EmiStatus;
import com.example.LoanRepayment.enums.RepaymentStatus;
import com.example.LoanRepayment.exception.DuplicateRepaymentException;
import com.example.LoanRepayment.repository.*;
import com.example.LoanRepayment.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RepaymentServiceImpl implements RepaymentService {
    private final LoanRepository loanRepository;
    private final EmiScheduleRepository emiRepository;
    private final LoanChargeRepository chargeRepository;
    private final RepaymentRepository repaymentRepository;
    private final LedgerEntryRepository ledgerRepository;

    @Override
    public RepaymentResponse processRepayment(RepaymentRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId()).orElseThrow(() -> new RuntimeException("Loan not found"));

        repaymentRepository.findByReferenceId(request.getReferenceId()).ifPresent(r -> {
            throw new DuplicateRepaymentException(
                    "Repayment already processed");
        });

        Repayment repayment = Repayment.builder().loan(loan).referenceId(request.getReferenceId()).amount(request.getAmount())
                .paymentDate(request.getPaymentDate()).status(RepaymentStatus.PROCESSED).build();

        repaymentRepository.save(repayment);

        BigDecimal remaining = request.getAmount();

        remaining = allocateCharges(loan, repayment, remaining);

        remaining = allocateInterest(loan, repayment, remaining);

        remaining = allocatePrincipal(loan, repayment, remaining);

        return RepaymentResponse.builder().referenceId(repayment.getReferenceId()).allocatedAmount(request.getAmount()
                .subtract(remaining)).message("Repayment processed successfully").build();
    }

    @Override
    public RepaymentReversalResponse reverseRepayment(Long repaymentId) {
        Repayment repayment = repaymentRepository.findById(repaymentId).orElseThrow(() -> new RuntimeException("Repayment not found"));

        if (repayment.getStatus() == RepaymentStatus.REVERSED) {
            throw new RuntimeException("Repayment already reversed");
        }

        List<LedgerEntry> ledgerEntries = ledgerRepository.findByRepaymentReference(repayment.getReferenceId());

        Collections.reverse(ledgerEntries);

        for (LedgerEntry entry : ledgerEntries) {

            if ("CHARGE".equals(entry.getEntityType())) {

                LoanCharge charge = chargeRepository.findById(entry.getEntityId())
                        .orElseThrow(() -> new RuntimeException("Charge not found"));

                charge.setAmountPaid(charge.getAmountPaid().subtract(entry.getAmount()));
                charge.setOutstandingAmount(charge.getOutstandingAmount().add(entry.getAmount()));
                updateChargeStatus(charge);
                chargeRepository.save(charge);
            } else if ("EMI".equals(entry.getEntityType())) {

                EmiSchedule emi = emiRepository.findById(entry.getEntityId()).orElseThrow(() -> new RuntimeException("EMI not found"));

                if ("INTEREST".equals(entry.getComponent())) {

                    emi.setInterestPaid(emi.getInterestPaid().subtract(entry.getAmount()));
                } else if ("PRINCIPAL".equals(entry.getComponent())) {

                    emi.setPrincipalPaid(emi.getPrincipalPaid().subtract(entry.getAmount()));
                }

                updateEmiStatus(emi);
                emiRepository.save(emi);
            }
        }

        repayment.setStatus(RepaymentStatus.REVERSED);

        repaymentRepository.save(repayment);

        return RepaymentReversalResponse.builder().repaymentReference(repayment.getReferenceId())
                .status("SUCCESS").message("Repayment reversed successfully").build();

    }

    private void updateEmiStatus(EmiSchedule emi) {

        BigDecimal principalOutstanding = emi.getPrincipalDue().subtract(emi.getPrincipalPaid());

        BigDecimal interestOutstanding = emi.getInterestDue().subtract(emi.getInterestPaid());

        if (principalOutstanding.compareTo(BigDecimal.ZERO) == 0
                && interestOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            emi.setStatus(EmiStatus.PAID);
        } else if (emi.getPrincipalPaid().compareTo(BigDecimal.ZERO) > 0
                || emi.getInterestPaid().compareTo(BigDecimal.ZERO) > 0) {

            emi.setStatus(EmiStatus.PARTIALLY_PAID);
        } else {
            emi.setStatus(EmiStatus.PENDING);
        }
    }

    private BigDecimal allocateCharges(Loan loan, Repayment repayment, BigDecimal remaining) {

        List<LoanCharge> charges = loan.getCharges();

        for (LoanCharge charge : charges) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal outstanding = charge.getOutstandingAmount();

            if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal allocation = remaining.min(outstanding);

            charge.setAmountPaid(charge.getAmountPaid().add(allocation));

            charge.setOutstandingAmount(charge.getOutstandingAmount().subtract(allocation));
            updateChargeStatus(charge);


            remaining = remaining.subtract(allocation);

            ledgerRepository.save(LedgerEntry.builder().repaymentReference(repayment.getReferenceId())
                    .entityType("CHARGE").entityId(charge.getId()).component(charge.getChargeType().name())
                    .amount(allocation).createdDate(LocalDateTime.now()).build());

            chargeRepository.save(charge);
        }

        return remaining;
    }

    private BigDecimal allocateInterest(Loan loan, Repayment repayment, BigDecimal remaining) {

        List<EmiSchedule> emis = loan.getEmiSchedules();

        emis.sort(Comparator.comparing(EmiSchedule::getDueDate));

        for (EmiSchedule emi : emis) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal outstanding = emi.getInterestDue().subtract(emi.getInterestPaid());

            if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal allocation = remaining.min(outstanding);

            emi.setInterestPaid(emi.getInterestPaid().add(allocation));
            updateEmiStatus(emi);
            emiRepository.save(emi);

            remaining = remaining.subtract(allocation);

            ledgerRepository.save(LedgerEntry.builder().repaymentReference(repayment.getReferenceId())
                    .entityType("EMI").entityId(emi.getId()).component("INTEREST").amount(allocation)
                    .createdDate(LocalDateTime.now()).build());
        }

        return remaining;
    }

    private BigDecimal allocatePrincipal(Loan loan, Repayment repayment, BigDecimal remaining) {

        List<EmiSchedule> emis = loan.getEmiSchedules();

        emis.sort(Comparator.comparing(EmiSchedule::getDueDate));

        for (EmiSchedule emi : emis) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal outstanding = emi.getPrincipalDue().subtract(emi.getPrincipalPaid());

            if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal allocation = remaining.min(outstanding);

            emi.setPrincipalPaid(emi.getPrincipalPaid().add(allocation));
            updateEmiStatus(emi);
            emiRepository.save(emi);

            remaining = remaining.subtract(allocation);

            ledgerRepository.save(LedgerEntry.builder().repaymentReference(repayment.getReferenceId())
                    .entityType("EMI").entityId(emi.getId()).component("PRINCIPAL").amount(allocation)
                    .createdDate(LocalDateTime.now()).build());
        }

        return remaining;
    }

    private void updateChargeStatus(LoanCharge charge) {

        if (charge.getOutstandingAmount().compareTo(BigDecimal.ZERO) == 0) {

            charge.setStatus(ChargeStatus.PAID);
        } else if (charge.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {

            charge.setStatus(ChargeStatus.PARTIALLY_PAID);
        } else {

            charge.setStatus(ChargeStatus.PENDING);
        }
    }
}
