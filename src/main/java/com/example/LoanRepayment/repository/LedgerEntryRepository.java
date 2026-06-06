package com.example.LoanRepayment.repository;

import com.example.LoanRepayment.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByRepaymentReference(String repaymentReference);
}
