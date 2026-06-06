package com.example.LoanRepayment.service.impl;

import com.example.LoanRepayment.dto.RepaymentRequest;
import com.example.LoanRepayment.dto.RepaymentResponse;
import com.example.LoanRepayment.entity.*;
import com.example.LoanRepayment.exception.DuplicateRepaymentException;
import com.example.LoanRepayment.repository.LedgerEntryRepository;
import com.example.LoanRepayment.repository.LoanChargeRepository;
import com.example.LoanRepayment.repository.LoanRepository;
import com.example.LoanRepayment.repository.RepaymentRepository;
import com.example.LoanRepayment.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RepaymentServiceImpl implements RepaymentService {
    private final LoanRepository loanRepository;
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

        Repayment repayment = Repayment.builder().loanId(loan.getId()).referenceId(request.getReferenceId())
                .amount(request.getAmount()).paymentDate(request.getPaymentDate()).reversed(false).build();

        repaymentRepository.save(repayment);

        BigDecimal remaining = request.getAmount();

        /*
         * Charges
         */
        remaining = allocateCharges(loan, repayment, remaining);

        /*
         * Interest
         */
        remaining = allocateInterest(loan, repayment, remaining);

        /*
         * STEP-3
         * Principal
         */
        remaining = allocatePrincipal(loan, repayment, remaining);

        return RepaymentResponse.builder().referenceId(repayment.getReferenceId()).allocatedAmount(request.getAmount().subtract(remaining)).message("Repayment processed successfully").build();
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

            remaining = remaining.subtract(allocation);

            ledgerRepository.save(LedgerEntry.builder().repaymentReference(repayment.getReferenceId())
                    .entityType("EMI").entityId(emi.getId()).component("PRINCIPAL").amount(allocation)
                    .createdDate(LocalDateTime.now()).build());
        }

        return remaining;
    }
}
