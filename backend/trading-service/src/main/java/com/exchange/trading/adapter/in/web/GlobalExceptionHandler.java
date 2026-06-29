package com.exchange.trading.adapter.in.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.error.HttpErrorMapper;
import com.exchange.trading.domain.exception.DuplicateClientOrderException;
import com.exchange.trading.domain.exception.OrderAccessDeniedException;
import com.exchange.trading.domain.exception.OrderNotFoundException;
import com.exchange.trading.domain.exception.TradingPairNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExchangeException.class)
    public ProblemDetail handleExchange(ExchangeException ex) {
        return HttpErrorMapper.toProblemDetail(ex);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.ORDER_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TradingPairNotFoundException.class)
    public ProblemDetail handlePairNotFound(TradingPairNotFoundException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ProblemDetail handleAccessDenied(OrderAccessDeniedException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.PERMISSION_DENIED, ex.getMessage());
    }

    @ExceptionHandler(DuplicateClientOrderException.class)
    public ProblemDetail handleDuplicate(DuplicateClientOrderException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.ALREADY_EXISTS, ex.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handleMissingHeader(MissingRequestHeaderException ex) {
        return HttpErrorMapper.toProblemDetail(ErrorCode.UNAUTHENTICATED, "Missing required header: " + ex.getHeaderName());
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
