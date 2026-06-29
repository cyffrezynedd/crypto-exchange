package com.exchange.clearing.adapter.in.web.dto;

import com.exchange.clearing.port.in.SettleTradeResult;

import java.util.UUID;

public record SettleTradeResponse(UUID tradeId, boolean settled) {

    public static SettleTradeResponse from(SettleTradeResult result) {
        return new SettleTradeResponse(result.tradeId(), result.settled());
    }
}
