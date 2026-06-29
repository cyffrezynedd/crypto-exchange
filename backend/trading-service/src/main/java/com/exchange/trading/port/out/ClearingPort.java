package com.exchange.trading.port.out;

import com.exchange.trading.domain.model.Trade;
import com.exchange.trading.domain.model.TradingPair;

import java.math.BigDecimal;
import java.util.UUID;

public interface ClearingPort {

    void freezeFunds(UUID orderId, Long userId, Long currencyId, BigDecimal amount);

    void unfreezeFunds(UUID orderId, Long userId, Long currencyId, BigDecimal amount);

    void settleTrade(Trade trade, TradingPair pair);
}
