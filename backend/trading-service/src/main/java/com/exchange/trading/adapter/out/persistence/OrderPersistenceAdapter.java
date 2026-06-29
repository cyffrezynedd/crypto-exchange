package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.Order;
import com.exchange.trading.port.out.OrderRepositoryPort;
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
    public boolean existsByUserIdAndClientOrderId(Long userId, String clientOrderId) {
        return repository.existsByUserIdAndClientOrderId(userId, clientOrderId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
