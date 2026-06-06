package com.example.LoanRepayment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RepaymentResponse {
    private String referenceId;

    private BigDecimal allocatedAmount;

    private String message;
}
