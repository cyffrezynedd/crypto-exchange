package com.exchange.trading.adapter.in.web.dto;

import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.domain.model.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PlaceOrderRequest(
        @NotNull Long tradingPairId,
        String clientOrderId,
        @NotNull OrderSide side,
        @NotNull OrderType type,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity
) {
    public com.exchange.trading.port.in.PlaceOrderCommand toCommand(Long userId) {
        return new com.exchange.trading.port.in.PlaceOrderCommand(
                userId,
                tradingPairId,
                clientOrderId,
                side,
                type,
                price,
                quantity
        );
    }
}
