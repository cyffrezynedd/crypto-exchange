package com.exchange.trading.port.in;

import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;

public record OrderSearchQuery(
        Long userId,
        OrderSide side,
        OrderStatus status,
        Long tradingPairId,
        int page,
        int size
) {
}
