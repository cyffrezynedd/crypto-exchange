package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

class OrderSpecifications {

    private OrderSpecifications() {
    }

    static Specification<OrderJpaEntity> forUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    static Specification<OrderJpaEntity> withSide(OrderSide side) {
        return (root, query, cb) -> cb.equal(root.get("side"), side);
    }

    static Specification<OrderJpaEntity> withStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    static Specification<OrderJpaEntity> withTradingPairId(Long tradingPairId) {
        return (root, query, cb) -> cb.equal(root.get("tradingPairId"), tradingPairId);
    }
}
