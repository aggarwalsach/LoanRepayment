package com.example.LoanRepayment.entity;

import com.example.LoanRepayment.enums.ChargeStatus;
import com.example.LoanRepayment.enums.ChargeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class LoanCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChargeType chargeType;

    private BigDecimal amount;

    private BigDecimal amountPaid;

    private BigDecimal outstandingAmount;

    @Enumerated(EnumType.STRING)
    private ChargeStatus status;

    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

}
