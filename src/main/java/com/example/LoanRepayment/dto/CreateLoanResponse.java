package com.example.LoanRepayment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateLoanResponse {
    private Long loanId;

    private String loanNumber;

    private String message;

}
