package com.exchange.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TradeExecutedEvent(
        UUID tradeId,
        Long tradingPairId,
        UUID buyOrderId,
        UUID sellOrderId,
        Long buyerId,
        Long sellerId,
        UUID makerOrderId,
        UUID takerOrderId,
        String price,
        String quantity,
        Instant executedAt
) {
}
