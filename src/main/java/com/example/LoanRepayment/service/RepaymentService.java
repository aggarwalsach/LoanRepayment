package com.example.LoanRepayment.service;

import com.example.LoanRepayment.dto.RepaymentRequest;
import com.example.LoanRepayment.dto.RepaymentResponse;
import com.example.LoanRepayment.dto.RepaymentReversalResponse;
import org.springframework.stereotype.Service;

public interface RepaymentService {

    RepaymentResponse processRepayment(
            RepaymentRequest request);

    RepaymentReversalResponse reverseRepayment(
            Long repaymentId);
}
