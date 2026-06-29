package com.exchange.common.clearing;

import java.math.BigDecimal;

public record FreezeFundsRequest(
        String idempotencyKey,
        Long userId,
        Long currencyId,
        BigDecimal amount,
        String reason
) {
}
