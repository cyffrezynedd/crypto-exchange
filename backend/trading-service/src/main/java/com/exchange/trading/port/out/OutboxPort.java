package com.exchange.trading.port.out;

import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.Trade;

public interface OutboxPort {

    void saveOrderCreated(Order order);

    void saveOrderCancelled(Order order);

    void saveTradeExecuted(Trade trade);
}
