package com.exchange.common.error;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ExchangeException extends RuntimeException {

    private final ErrorCode code;
    private final Map<String, Object> details;

    public ExchangeException(ErrorCode code, String message) {
        this(code, message, Collections.emptyMap(), null);
    }

    public ExchangeException(ErrorCode code, String message, Map<String, Object> details) {
        this(code, message, details, null);
    }

    public ExchangeException(ErrorCode code, String message, Throwable cause) {
        this(code, message, Collections.emptyMap(), cause);
    }

    public ExchangeException(ErrorCode code, String message, Map<String, Object> details, Throwable cause) {
        super(message, cause);
        this.code = Objects.requireNonNull(code, "code");
        this.details = details == null || details.isEmpty()
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    public ErrorCode code() {
        return code;
    }

    public Map<String, Object> details() {
        return details;
    }
}
