package com.example.LoanRepayment.controller;

import com.example.LoanRepayment.dto.AddChargeRequest;
import com.example.LoanRepayment.dto.AddChargeResponse;
import com.example.LoanRepayment.service.ChargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class ChargeController {

    private final ChargeService chargeService;

    @PostMapping("/{loanId}/charges")
    @ResponseStatus(HttpStatus.CREATED)
    public AddChargeResponse addCharge(@PathVariable Long loanId,
                                       @Valid @RequestBody AddChargeRequest request) {

        return chargeService.addCharge(loanId, request);
    }
}
