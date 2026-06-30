package com.exchange.trading.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
class UserFavoritePairId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "trading_pair_id")
    private Long tradingPairId;

    protected UserFavoritePairId() {
    }

    UserFavoritePairId(Long userId, Long tradingPairId) {
        this.userId = userId;
        this.tradingPairId = tradingPairId;
    }

    Long getUserId() {
        return userId;
    }

    Long getTradingPairId() {
        return tradingPairId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserFavoritePairId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && Objects.equals(tradingPairId, that.tradingPairId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tradingPairId);
    }
}
