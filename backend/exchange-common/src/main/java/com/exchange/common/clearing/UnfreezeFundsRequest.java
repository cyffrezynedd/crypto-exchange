package com.exchange.common.clearing;

import java.math.BigDecimal;

public record UnfreezeFundsRequest(
        String idempotencyKey,
        Long userId,
        Long currencyId,
        BigDecimal amount,
        String reason
) {
}
