package com.example.LoanRepayment.repository;

import com.example.LoanRepayment.entity.EmiSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {
    List<EmiSchedule> findByLoanId(Long loanId);

}
