package com.exchange.common.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpErrorMapperTest {

    @Test
    void mapsExchangeExceptionToProblemDetail() {
        ExchangeException exception = new ExchangeException(ErrorCode.INSUFFICIENT_FUNDS, "Not enough USDT");

        ProblemDetail detail = HttpErrorMapper.toProblemDetail(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), detail.getStatus());
        assertEquals("INSUFFICIENT_FUNDS", detail.getTitle());
        assertEquals("Not enough USDT", detail.getDetail());
        assertEquals("INSUFFICIENT_FUNDS", detail.getProperties().get("code"));
    }

    @Test
    void mapsErrorCodeWithDetails() {
        ProblemDetail detail = HttpErrorMapper.toProblemDetail(
                ErrorCode.VALIDATION_FAILED,
                "Invalid quantity",
                Map.of("field", "quantity"));

        assertEquals(HttpStatus.BAD_REQUEST.value(), detail.getStatus());
        assertEquals("quantity", ((Map<?, ?>) detail.getProperties().get("details")).get("field"));
    }
}
