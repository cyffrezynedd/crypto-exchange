package com.exchange.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.Map;

public final class HttpErrorMapper {

    private HttpErrorMapper() {
    }

    public static ProblemDetail toProblemDetail(ExchangeException exception) {
        ErrorCode code = exception.code();
        HttpStatus status = code.httpStatus();
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        detail.setTitle(code.name());
        detail.setProperty("code", code.name());
        if (!exception.details().isEmpty()) {
            detail.setProperty("details", exception.details());
        }
        return detail;
    }

    public static ProblemDetail toProblemDetail(ErrorCode code, String message) {
        return toProblemDetail(code, message, Map.of());
    }

    public static ProblemDetail toProblemDetail(ErrorCode code, String message, Map<String, Object> details) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(code.httpStatus(), message);
        detail.setTitle(code.name());
        detail.setProperty("code", code.name());
        if (!details.isEmpty()) {
            detail.setProperty("details", details);
        }
        return detail;
    }

    public static ProblemDetail toProblemDetail(ErrorCode code, String message, URI type) {
        ProblemDetail detail = toProblemDetail(code, message);
        detail.setType(type);
        return detail;
    }
}
