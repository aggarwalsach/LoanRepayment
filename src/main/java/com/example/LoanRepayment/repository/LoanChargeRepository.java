package com.example.LoanRepayment.repository;

import com.example.LoanRepayment.entity.LoanCharge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanChargeRepository extends JpaRepository<LoanCharge, Long> {
}
