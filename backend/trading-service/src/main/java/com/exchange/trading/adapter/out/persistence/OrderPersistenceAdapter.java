package com.exchange.trading.adapter.out.persistence;

import com.exchange.common.web.PageResponse;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.port.out.OrderRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repository;

    OrderPersistenceAdapter(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return repository.save(OrderJpaEntity.fromDomain(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(OrderJpaEntity::toDomain);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }

    @Override
    public PageResponse<Order> search(
            Long userId,
            OrderSide side,
            OrderStatus status,
            Long tradingPairId,
            int page,
            int size
    ) {
        Specification<OrderJpaEntity> spec = OrderSpecifications.forUser(userId);
        if (side != null) {
            spec = spec.and(OrderSpecifications.withSide(side));
        }
        if (status != null) {
            spec = spec.and(OrderSpecifications.withStatus(status));
        }
        if (tradingPairId != null) {
            spec = spec.and(OrderSpecifications.withTradingPairId(tradingPairId));
        }
        Page<OrderJpaEntity> result = repository.findAll(
                spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return new PageResponse<>(
                result.getContent().stream().map(OrderJpaEntity::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public boolean existsByUserIdAndClientOrderId(Long userId, String clientOrderId) {
        return repository.existsByUserIdAndClientOrderId(userId, clientOrderId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
