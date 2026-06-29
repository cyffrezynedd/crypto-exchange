package com.exchange.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    ALREADY_EXISTS(HttpStatus.CONFLICT),
    CONFLICT(HttpStatus.CONFLICT),
    INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND),
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND),
    BUSINESS_RULE_VIOLATED(HttpStatus.UNPROCESSABLE_ENTITY),
    PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),
    UPSTREAM_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT),
    RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS);

    private final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
