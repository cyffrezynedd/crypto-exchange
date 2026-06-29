package com.exchange.trading.port.out;

import com.exchange.trading.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByUserId(Long userId);

    boolean existsByUserIdAndClientOrderId(Long userId, String clientOrderId);

    void deleteById(UUID id);
}
