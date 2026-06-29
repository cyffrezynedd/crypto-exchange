package com.exchange.trading.port.out;

import com.exchange.trading.domain.model.Trade;

public interface TradeRepositoryPort {

    Trade save(Trade trade);
}
