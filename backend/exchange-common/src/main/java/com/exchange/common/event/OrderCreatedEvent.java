package com.exchange.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreatedEvent(
        UUID orderId,
        Long userId,
        Long tradingPairId,
        String clientOrderId,
        String side,
        String type,
        String price,
        String quantity,
        String status,
        Instant createdAt
) {
}
