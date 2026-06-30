package com.exchange.trading.port.in;

public record FavoritePairSearchQuery(
        Long userId,
        Long tradingPairId,
        String symbol,
        int page,
        int size
) {
}
