package com.exchange.trading.port.out;

import com.exchange.trading.domain.model.MatchResult;
import com.exchange.trading.domain.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchingEnginePort {

    List<MatchResult> submitOrder(Order order);

    void removeOrder(UUID orderId, Long tradingPairId);

    Optional<BigDecimal> getBestAskPrice(Long tradingPairId);
}
