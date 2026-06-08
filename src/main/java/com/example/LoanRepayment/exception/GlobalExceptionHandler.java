package com.example.LoanRepayment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleLoanNotFound(LoanNotFoundException ex) {

        return ex.getMessage();
    }

    @ExceptionHandler(DuplicateRepaymentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateRepayment(DuplicateRepaymentException ex) {

        return ex.getMessage();
    }

    @ExceptionHandler(RepaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String repaymentNotFound(RepaymentNotFoundException ex) {

        return ex.getMessage();
    }

    @ExceptionHandler(TenureAndEMIMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String tenureAndEMIMismatch(TenureAndEMIMismatchException ex) {

        return ex.getMessage();
    }
}
