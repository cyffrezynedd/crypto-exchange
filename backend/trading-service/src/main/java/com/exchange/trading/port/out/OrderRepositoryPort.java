package com.exchange.trading.port.out;

import com.exchange.common.web.PageResponse;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByUserId(Long userId);

    PageResponse<Order> search(
            Long userId,
            OrderSide side,
            OrderStatus status,
            Long tradingPairId,
            int page,
            int size
    );

    boolean existsByUserIdAndClientOrderId(Long userId, String clientOrderId);

    void deleteById(UUID id);
}
