package com.exchange.trading.domain.model;

import java.math.BigDecimal;

public record MatchResult(
        Order takerOrder,
        Order makerOrder,
        BigDecimal price,
        BigDecimal quantity
) {
}
