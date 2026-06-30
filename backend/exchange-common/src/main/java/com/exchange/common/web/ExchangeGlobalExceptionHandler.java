package com.exchange.common.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.error.HttpErrorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class ExchangeGlobalExceptionHandler {

    @ExceptionHandler(ExchangeException.class)
    public ProblemDetail handleExchange(ExchangeException ex) {
        return HttpErrorMapper.toProblemDetail(ex);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handleMissingHeader(MissingRequestHeaderException ex) {
        return HttpErrorMapper.toProblemDetail(
                ErrorCode.UNAUTHENTICATED,
                "Missing required header: " + ex.getHeaderName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle(ErrorCode.VALIDATION_FAILED.name());
        detail.setProperty("code", ErrorCode.VALIDATION_FAILED.name());
        detail.setDetail("Validation failed");
        return detail;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.CONFLICT, ex.getMessage());
    }
}
