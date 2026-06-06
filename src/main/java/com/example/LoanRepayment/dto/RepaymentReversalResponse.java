package com.example.LoanRepayment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentReversalResponse {

    private String repaymentReference;

    private String status;

    private String message;

}
