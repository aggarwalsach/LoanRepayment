package com.example.LoanRepayment.dto;

import jakarta.validation.Valid;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateLoanRequest {

    @NotNull
    private BigDecimal loanAmount;

    @NotNull
    private BigDecimal interestAmount;

    @NotNull
    private Integer tenure;

    @Valid
    private List<EmiScheduleRequest> emiSchedules;

}
