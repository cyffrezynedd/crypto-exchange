package com.exchange.marketdata.adapter.in.web.dto;

public record TradeResponse(String tradeId, String price, String quantity, String executedAt) {
}
