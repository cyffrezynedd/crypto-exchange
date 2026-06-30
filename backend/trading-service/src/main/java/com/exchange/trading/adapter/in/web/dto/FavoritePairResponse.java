package com.exchange.trading.adapter.in.web.dto;

import com.exchange.trading.domain.model.FavoritePair;

import java.time.Instant;

public record FavoritePairResponse(
        Long userId,
        Long tradingPairId,
        String symbol,
        Instant addedAt
) {
    public static FavoritePairResponse from(FavoritePair favorite) {
        return new FavoritePairResponse(
                favorite.getUserId(),
                favorite.getTradingPairId(),
                favorite.getSymbol(),
                favorite.getAddedAt()
        );
    }
}
