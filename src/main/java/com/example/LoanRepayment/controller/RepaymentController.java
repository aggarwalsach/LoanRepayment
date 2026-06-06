package com.example.LoanRepayment.controller;

import com.example.LoanRepayment.dto.RepaymentRequest;
import com.example.LoanRepayment.dto.RepaymentResponse;
import com.example.LoanRepayment.dto.RepaymentReversalResponse;
import com.example.LoanRepayment.service.RepaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repayments")
@RequiredArgsConstructor

public class RepaymentController {

    private final RepaymentService repaymentService;

    @PostMapping
    public RepaymentResponse processRepayment(@Valid @RequestBody RepaymentRequest request) {

        return repaymentService.processRepayment(request);
    }

    @PostMapping("/{repaymentId}/reverse")
    public ResponseEntity<RepaymentReversalResponse> reverseRepayment(@PathVariable Long repaymentId) {

        return ResponseEntity.ok(repaymentService.reverseRepayment(repaymentId));
    }
}
