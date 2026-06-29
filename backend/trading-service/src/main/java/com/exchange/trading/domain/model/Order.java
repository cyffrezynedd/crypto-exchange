package com.exchange.trading.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order {

    private UUID id;
    private Long userId;
    private Long tradingPairId;
    private String clientOrderId;
    private OrderSide side;
    private OrderType type;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal filledQuantity;
    private BigDecimal lockedAmount;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
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

    public BigDecimal getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(BigDecimal filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public BigDecimal getLockedAmount() {
        return lockedAmount;
    }

    public void setLockedAmount(BigDecimal lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal remainingQuantity() {
        return quantity.subtract(filledQuantity);
    }

    public boolean isActive() {
        return status == OrderStatus.NEW || status == OrderStatus.PARTIALLY_FILLED;
    }

    public void applyFill(BigDecimal fillQuantity) {
        filledQuantity = filledQuantity.add(fillQuantity);
        if (filledQuantity.compareTo(quantity) == 0) {
            status = OrderStatus.FILLED;
        } else if (filledQuantity.compareTo(BigDecimal.ZERO) > 0) {
            status = OrderStatus.PARTIALLY_FILLED;
        }
    }
}
