package com.exchange.trading.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Trade {

    private UUID id;
    private Long tradingPairId;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private Long buyerId;
    private Long sellerId;
    private UUID makerOrderId;
    private UUID takerOrderId;
    private BigDecimal price;
    private BigDecimal quantity;
    private Instant executedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getTradingPairId() {
        return tradingPairId;
    }

    public void setTradingPairId(Long tradingPairId) {
        this.tradingPairId = tradingPairId;
    }

    public UUID getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(UUID buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public UUID getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(UUID sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public UUID getMakerOrderId() {
        return makerOrderId;
    }

    public void setMakerOrderId(UUID makerOrderId) {
        this.makerOrderId = makerOrderId;
    }

    public UUID getTakerOrderId() {
        return takerOrderId;
    }

    public void setTakerOrderId(UUID takerOrderId) {
        this.takerOrderId = takerOrderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }
}
