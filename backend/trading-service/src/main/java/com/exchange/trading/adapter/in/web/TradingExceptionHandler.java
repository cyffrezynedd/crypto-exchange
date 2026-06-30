package com.exchange.trading.adapter.in.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.HttpErrorMapper;
import com.exchange.common.web.ExchangeGlobalExceptionHandler;
import com.exchange.trading.domain.exception.DuplicateClientOrderException;
import com.exchange.trading.domain.exception.OrderAccessDeniedException;
import com.exchange.trading.domain.exception.OrderNotFoundException;
import com.exchange.trading.domain.exception.TradingPairNotFoundException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TradingExceptionHandler extends ExchangeGlobalExceptionHandler {

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
}
