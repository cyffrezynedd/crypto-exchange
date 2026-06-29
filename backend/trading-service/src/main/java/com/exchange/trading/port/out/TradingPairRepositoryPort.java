package com.exchange.trading.port.out;

import com.exchange.trading.domain.model.TradingPair;

import java.util.Optional;

public interface TradingPairRepositoryPort {

    Optional<TradingPair> findById(Long id);
}
