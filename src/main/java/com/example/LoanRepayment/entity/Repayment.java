package com.example.LoanRepayment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repayment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long loanId;

    @Column(unique = true)
    private String referenceId;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private Boolean reversed;
}
