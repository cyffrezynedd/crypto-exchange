package com.exchange.trading.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface TradeJpaRepository extends JpaRepository<TradeJpaEntity, UUID> {
}
