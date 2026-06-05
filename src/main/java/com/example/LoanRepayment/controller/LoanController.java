package com.example.LoanRepayment.controller;

import com.example.LoanRepayment.dto.CreateLoanRequest;
import com.example.LoanRepayment.dto.CreateLoanResponse;
import com.example.LoanRepayment.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor

public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateLoanResponse createLoan(
            @Valid
            @RequestBody
            CreateLoanRequest request) {

        return loanService.createLoan(request);

    }
}
