package com.exchange.common.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorCodeTest {

    @Test
    void mapsDomainErrorsToExpectedHttpStatus() {
        assertEquals(HttpStatus.NOT_FOUND, ErrorCode.ORDER_NOT_FOUND.httpStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.INSUFFICIENT_FUNDS.httpStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_INVALID.httpStatus());
        assertEquals(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS.httpStatus());
    }
}
