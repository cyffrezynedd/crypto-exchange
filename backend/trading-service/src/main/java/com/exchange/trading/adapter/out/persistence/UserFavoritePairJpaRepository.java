package com.exchange.trading.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserFavoritePairJpaRepository extends JpaRepository<UserFavoritePairJpaEntity, UserFavoritePairId> {

    @Query("""
            SELECT f FROM UserFavoritePairJpaEntity f
            JOIN TradingPairJpaEntity p ON p.id = f.id.tradingPairId
            WHERE f.id.userId = :userId
              AND (:tradingPairId IS NULL OR f.id.tradingPairId = :tradingPairId)
              AND (:symbolPattern IS NULL OR LOWER(p.symbol) LIKE :symbolPattern)
            ORDER BY f.addedAt DESC
            """)
    Page<UserFavoritePairJpaEntity> search(
            @Param("userId") Long userId,
            @Param("tradingPairId") Long tradingPairId,
            @Param("symbolPattern") String symbolPattern,
            Pageable pageable
    );
}
