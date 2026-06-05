package com.example.LoanRepayment.entity;

import com.example.LoanRepayment.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loanNumber;

    private BigDecimal loanAmount;

    private BigDecimal interestAmount;

    private Integer tenure;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @OneToMany(
            mappedBy = "loan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    @Builder.Default
    private List<EmiSchedule> emiSchedules = new ArrayList<>();
}
