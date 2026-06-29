package com.exchange.common.clearing;

import java.math.BigDecimal;
import java.util.UUID;

public record SettleTradeRequest(
        UUID eventId,
        UUID tradeId,
        Long buyerId,
        Long sellerId,
        Long quoteCurrencyId,
        Long baseCurrencyId,
        BigDecimal price,
        BigDecimal quantity,
        BigDecimal feeRate
) {
}
