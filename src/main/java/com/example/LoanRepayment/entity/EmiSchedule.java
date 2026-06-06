package com.example.LoanRepayment.entity;

import com.example.LoanRepayment.enums.EmiStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal principalDue;

    private BigDecimal interestDue;

    @Builder.Default
    private BigDecimal principalPaid = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal interestPaid = BigDecimal.ZERO;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private EmiStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;
}
