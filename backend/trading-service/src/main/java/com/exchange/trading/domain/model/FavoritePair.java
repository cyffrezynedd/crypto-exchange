package com.exchange.trading.domain.model;

import java.time.Instant;

public class FavoritePair {

    private Long userId;
    private Long tradingPairId;
    private String symbol;
    private Instant addedAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTradingPairId() {
        return tradingPairId;
    }

    public void setTradingPairId(Long tradingPairId) {
        this.tradingPairId = tradingPairId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }
}
