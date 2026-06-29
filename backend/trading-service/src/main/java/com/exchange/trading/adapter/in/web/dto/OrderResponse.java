package com.exchange.trading.adapter.in.web.dto;

import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.domain.model.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        Long userId,
        Long tradingPairId,
        String clientOrderId,
        OrderSide side,
        OrderType type,
        BigDecimal price,
        BigDecimal quantity,
        BigDecimal filledQuantity,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTradingPairId(),
                order.getClientOrderId(),
                order.getSide(),
                order.getType(),
                order.getPrice(),
                order.getQuantity(),
                order.getFilledQuantity(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
