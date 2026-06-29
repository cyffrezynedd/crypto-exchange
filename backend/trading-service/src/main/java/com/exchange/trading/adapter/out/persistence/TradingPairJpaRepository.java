package com.exchange.trading.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface TradingPairJpaRepository extends JpaRepository<TradingPairJpaEntity, Long> {
}
