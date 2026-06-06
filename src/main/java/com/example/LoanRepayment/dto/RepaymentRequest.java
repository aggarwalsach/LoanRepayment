package com.example.LoanRepayment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

public class RepaymentRequest {

    @NotNull
    private Long loanId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDateTime paymentDate;

    @NotNull
    private String referenceId;

}
