package com.example.LoanRepayment.repository;

import com.example.LoanRepayment.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
    Optional<Repayment>
    findByReferenceId(String referenceId);
}
