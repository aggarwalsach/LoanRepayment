package com.example.LoanRepayment.service;

import com.example.LoanRepayment.dto.AddChargeRequest;
import com.example.LoanRepayment.dto.AddChargeResponse;

public interface ChargeService {

    AddChargeResponse addCharge(
            Long loanId,
            AddChargeRequest request);

}
