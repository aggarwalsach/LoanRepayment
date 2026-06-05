package com.example.LoanRepayment.service.impl;

import com.example.LoanRepayment.dto.AddChargeRequest;
import com.example.LoanRepayment.dto.AddChargeResponse;
import com.example.LoanRepayment.entity.Loan;
import com.example.LoanRepayment.entity.LoanCharge;
import com.example.LoanRepayment.enums.ChargeStatus;
import com.example.LoanRepayment.exception.LoanNotFoundException;
import com.example.LoanRepayment.repository.LoanChargeRepository;
import com.example.LoanRepayment.repository.LoanRepository;
import com.example.LoanRepayment.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class ChargeServiceImpl implements ChargeService {
    private final LoanRepository loanRepository;
    private final LoanChargeRepository chargeRepository;

    @Override
    @Transactional
    public AddChargeResponse addCharge(Long loanId, AddChargeRequest request) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        LoanCharge charge = LoanCharge.builder().chargeType(request.getChargeType())
                .amount(request.getAmount()).amountPaid(BigDecimal.ZERO)
                .outstandingAmount(request.getAmount()).status(ChargeStatus.PENDING)
                .createdDate(LocalDateTime.now()).loan(loan).build();
        LoanCharge saved =
                chargeRepository.save(charge);

        return AddChargeResponse.builder()
                .chargeId(saved.getId())
                .message(
                        "Charge added successfully")
                .build();
    }
}
