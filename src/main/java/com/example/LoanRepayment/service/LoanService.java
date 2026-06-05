package com.example.LoanRepayment.service;

import com.example.LoanRepayment.dto.CreateLoanRequest;
import com.example.LoanRepayment.dto.CreateLoanResponse;

public interface LoanService {
    CreateLoanResponse createLoan(
            CreateLoanRequest request);

}
