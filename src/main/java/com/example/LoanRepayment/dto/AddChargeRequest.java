package com.example.LoanRepayment.dto;

import com.example.LoanRepayment.enums.ChargeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddChargeRequest {
    @NotNull
    private ChargeType chargeType;

    @NotNull
    @Positive
    private BigDecimal amount;

}
