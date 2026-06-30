package com.exchange.iam.adapter.in.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.HttpErrorMapper;
import com.exchange.common.web.ExchangeGlobalExceptionHandler;
import com.exchange.iam.domain.exception.DuplicateUserException;
import com.exchange.iam.domain.exception.UserNotFoundException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IamExceptionHandler extends ExchangeGlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleDuplicate(DuplicateUserException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.ALREADY_EXISTS, ex.getMessage());
    }
}
