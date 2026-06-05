package com.example.LoanRepayment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class AddChargeResponse {

    private Long chargeId;

    private String message;

}
