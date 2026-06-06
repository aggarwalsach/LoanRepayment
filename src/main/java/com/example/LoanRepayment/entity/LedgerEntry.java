package com.example.LoanRepayment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repaymentReference;

    private String entityType;

    private Long entityId;

    private String component;

    private BigDecimal amount;

    private LocalDateTime createdDate;

}
