package com.exchange.iam.adapter.in.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.error.HttpErrorMapper;
import com.exchange.iam.domain.exception.DuplicateUserException;
import com.exchange.iam.domain.exception.UserNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleDuplicate(DuplicateUserException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.ALREADY_EXISTS, ex.getMessage());
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
