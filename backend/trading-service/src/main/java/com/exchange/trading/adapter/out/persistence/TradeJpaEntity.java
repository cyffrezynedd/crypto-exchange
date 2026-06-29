package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.Trade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
class TradeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "trading_pair_id", nullable = false)
    private Long tradingPairId;

    @Column(name = "buy_order_id", nullable = false)
    private UUID buyOrderId;

    @Column(name = "sell_order_id", nullable = false)
    private UUID sellOrderId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "maker_order_id", nullable = false)
    private UUID makerOrderId;

    @Column(name = "taker_order_id", nullable = false)
    private UUID takerOrderId;

    @Column(nullable = false, precision = 36, scale = 18)
    private BigDecimal price;

    @Column(nullable = false, precision = 36, scale = 18)
    private BigDecimal quantity;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    @PrePersist
    void onCreate() {
        if (executedAt == null) {
            executedAt = Instant.now();
        }
    }

    static TradeJpaEntity fromDomain(Trade trade) {
        TradeJpaEntity entity = new TradeJpaEntity();
        entity.id = trade.getId();
        entity.tradingPairId = trade.getTradingPairId();
        entity.buyOrderId = trade.getBuyOrderId();
        entity.sellOrderId = trade.getSellOrderId();
        entity.buyerId = trade.getBuyerId();
        entity.sellerId = trade.getSellerId();
        entity.makerOrderId = trade.getMakerOrderId();
        entity.takerOrderId = trade.getTakerOrderId();
        entity.price = trade.getPrice();
        entity.quantity = trade.getQuantity();
        entity.executedAt = trade.getExecutedAt();
        return entity;
    }
}
