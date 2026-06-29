package com.exchange.marketdata.adapter.in.web.dto;

import java.util.List;

public record OrderBookResponse(String symbol, List<OrderBookLevel> bids, List<OrderBookLevel> asks) {
}
