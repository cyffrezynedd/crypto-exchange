package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.domain.model.OrderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trading_pair_id", nullable = false)
    private Long tradingPairId;

    @Column(name = "client_order_id", length = 64)
    private String clientOrderId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "order_side")
    private OrderSide side;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "order_type")
    private OrderType type;

    @Column(precision = 36, scale = 18)
    private BigDecimal price;

    @Column(nullable = false, precision = 36, scale = 18)
    private BigDecimal quantity;

    @Column(name = "filled_quantity", nullable = false, precision = 36, scale = 18)
    private BigDecimal filledQuantity;

    @Column(name = "locked_amount", precision = 36, scale = 18)
    private BigDecimal lockedAmount;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "order_status")
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            Instant now = Instant.now();
            createdAt = now;
            updatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    static OrderJpaEntity fromDomain(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.id = order.getId();
        entity.userId = order.getUserId();
        entity.tradingPairId = order.getTradingPairId();
        entity.clientOrderId = order.getClientOrderId();
        entity.side = order.getSide();
        entity.type = order.getType();
        entity.price = order.getPrice();
        entity.quantity = order.getQuantity();
        entity.filledQuantity = order.getFilledQuantity();
        entity.lockedAmount = order.getLockedAmount();
        entity.status = order.getStatus();
        entity.createdAt = order.getCreatedAt();
        entity.updatedAt = order.getUpdatedAt();
        return entity;
    }

    Order toDomain() {
        Order order = new Order();
        order.setId(id);
        order.setUserId(userId);
        order.setTradingPairId(tradingPairId);
        order.setClientOrderId(clientOrderId);
        order.setSide(side);
        order.setType(type);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setFilledQuantity(filledQuantity);
        order.setLockedAmount(lockedAmount);
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);
        return order;
    }
}
