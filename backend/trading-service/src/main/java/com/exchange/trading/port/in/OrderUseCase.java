package com.exchange.trading.port.in;

import com.exchange.trading.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderUseCase {

    Order placeOrder(PlaceOrderCommand command);

    Order cancelOrder(CancelOrderCommand command);

    Order getOrder(Long userId, UUID orderId);

    List<Order> listOrders(Long userId);
}
