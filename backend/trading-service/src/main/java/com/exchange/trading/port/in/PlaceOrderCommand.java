package com.exchange.trading.port.in;

import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PlaceOrderCommand(
        Long userId,
        Long tradingPairId,
        String clientOrderId,
        OrderSide side,
        OrderType type,
        BigDecimal price,
        BigDecimal quantity
) {
}
