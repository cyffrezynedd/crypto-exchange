package com.exchange.clearing.adapter.in.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.error.HttpErrorMapper;
import com.exchange.clearing.domain.exception.InsufficientFundsException;
import com.exchange.clearing.domain.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExchangeException.class)
    public ProblemDetail handleExchange(ExchangeException ex) {
        return HttpErrorMapper.toProblemDetail(ex);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ProblemDetail handleWalletNotFound(WalletNotFoundException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.WALLET_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.INSUFFICIENT_FUNDS, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle(ErrorCode.VALIDATION_FAILED.name());
        detail.setProperty("code", ErrorCode.VALIDATION_FAILED.name());
        detail.setDetail("Validation failed");
        return detail;
    }
}
