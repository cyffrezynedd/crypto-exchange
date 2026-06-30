package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.FavoritePair;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "user_favorite_pairs", schema = "market")
class UserFavoritePairJpaEntity {

    @EmbeddedId
    private UserFavoritePairId id;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    UserFavoritePairId getId() {
        return id;
    }

    FavoritePair toDomain(String symbol) {
        FavoritePair favorite = new FavoritePair();
        favorite.setUserId(id.getUserId());
        favorite.setTradingPairId(id.getTradingPairId());
        favorite.setSymbol(symbol);
        favorite.setAddedAt(addedAt);
        return favorite;
    }

    static UserFavoritePairJpaEntity of(Long userId, Long tradingPairId, Instant addedAt) {
        UserFavoritePairJpaEntity entity = new UserFavoritePairJpaEntity();
        entity.id = new UserFavoritePairId(userId, tradingPairId);
        entity.addedAt = addedAt;
        return entity;
    }
}
