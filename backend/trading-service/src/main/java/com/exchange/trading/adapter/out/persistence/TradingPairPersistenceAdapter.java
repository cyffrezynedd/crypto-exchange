package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.TradingPair;
import com.exchange.trading.port.out.TradingPairRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class TradingPairPersistenceAdapter implements TradingPairRepositoryPort {

    private final TradingPairJpaRepository repository;

    TradingPairPersistenceAdapter(TradingPairJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TradingPair> findById(Long id) {
        return repository.findById(id).map(TradingPairJpaEntity::toDomain);
    }
}
