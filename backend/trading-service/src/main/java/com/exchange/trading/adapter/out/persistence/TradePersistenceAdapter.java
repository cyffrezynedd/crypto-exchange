package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.Trade;
import com.exchange.trading.port.out.TradeRepositoryPort;
import org.springframework.stereotype.Component;

@Component
class TradePersistenceAdapter implements TradeRepositoryPort {

    private final TradeJpaRepository repository;

    TradePersistenceAdapter(TradeJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Trade save(Trade trade) {
        repository.save(TradeJpaEntity.fromDomain(trade));
        return trade;
    }
}
