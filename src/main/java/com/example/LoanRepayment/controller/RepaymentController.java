package com.example.LoanRepayment.controller;

import com.example.LoanRepayment.dto.RepaymentRequest;
import com.example.LoanRepayment.dto.RepaymentResponse;
import com.example.LoanRepayment.service.RepaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repayments")
@RequiredArgsConstructor

public class RepaymentController {

    private final RepaymentService repaymentService;

    @PostMapping
    public RepaymentResponse processRepayment(@Valid @RequestBody RepaymentRequest request) {

        return repaymentService.processRepayment(request);
    }
}
