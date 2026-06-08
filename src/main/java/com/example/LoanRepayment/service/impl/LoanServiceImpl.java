package com.example.LoanRepayment.service.impl;

import com.example.LoanRepayment.dto.CreateLoanRequest;
import com.example.LoanRepayment.dto.CreateLoanResponse;
import com.example.LoanRepayment.dto.EmiScheduleRequest;
import com.example.LoanRepayment.entity.EmiSchedule;
import com.example.LoanRepayment.entity.Loan;
import com.example.LoanRepayment.enums.EmiStatus;
import com.example.LoanRepayment.enums.LoanStatus;
import com.example.LoanRepayment.exception.TenureAndEMIMismatchException;
import com.example.LoanRepayment.repository.LoanRepository;
import com.example.LoanRepayment.service.LoanService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Builder
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    @java.lang.Override
    @Transactional

    public CreateLoanResponse createLoan(CreateLoanRequest request) {

        validateLoan(request);

        Loan loan = Loan.builder().loanNumber(generateLoanNumber()).loanAmount(request.getLoanAmount())
                .interestAmount(request.getInterestAmount()).tenure(request.getTenure())
                .status(LoanStatus.ACTIVE).build();

        request.getEmiSchedules().forEach(schedule -> {

            EmiSchedule emi = EmiSchedule.builder().principalDue(schedule.getPrincipalDue())
                    .interestDue(schedule.getInterestDue()).principalPaid(BigDecimal.ZERO)
                    .interestPaid(BigDecimal.ZERO).status(EmiStatus.PENDING)
                    .dueDate(schedule.getDueDate()).loan(loan).build();


            loan.getEmiSchedules().add(emi);
        });

        Loan savedLoan = loanRepository.save(loan);

        return CreateLoanResponse.builder().loanId(savedLoan.getId()).loanNumber(savedLoan.getLoanNumber())
                .message("Loan created successfully").build();
    }

    private String generateLoanNumber() {
        return "LN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validateLoan(CreateLoanRequest request) {

        if (request.getEmiSchedules().size() != request.getTenure()) {

            throw new TenureAndEMIMismatchException("Tenure and EMI count mismatch");
        }

        BigDecimal totalPrincipal = request.getEmiSchedules().stream()
                .map(EmiScheduleRequest::getPrincipalDue).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrincipal.compareTo(request.getLoanAmount()) != 0) {

            throw new RuntimeException("Principal mismatch");
        }

        BigDecimal totalInterest = request.getEmiSchedules().stream()
                .map(EmiScheduleRequest::getInterestDue).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalInterest.compareTo(request.getInterestAmount()) != 0) {

            throw new RuntimeException("Interest mismatch");
        }
    }
}
