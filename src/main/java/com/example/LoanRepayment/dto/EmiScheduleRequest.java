package com.example.LoanRepayment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmiScheduleRequest {

    private BigDecimal principalDue;

    private BigDecimal interestDue;

    private LocalDate dueDate;
}
