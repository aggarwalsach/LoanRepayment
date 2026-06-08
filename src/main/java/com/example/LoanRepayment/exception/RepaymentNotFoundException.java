package com.example.LoanRepayment.exception;

public class RepaymentNotFoundException extends RuntimeException{
    public RepaymentNotFoundException(String message) {
        super(message);
    }
}
