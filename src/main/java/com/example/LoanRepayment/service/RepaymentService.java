package com.example.LoanRepayment.service;

import com.example.LoanRepayment.dto.RepaymentRequest;
import com.example.LoanRepayment.dto.RepaymentResponse;
import org.springframework.stereotype.Service;

public interface RepaymentService {

    RepaymentResponse processRepayment(
            RepaymentRequest request);
}
