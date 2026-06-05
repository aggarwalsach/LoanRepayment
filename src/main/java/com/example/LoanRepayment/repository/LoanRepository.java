package com.example.LoanRepayment.repository;

import com.example.LoanRepayment.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan,Long> {
}
