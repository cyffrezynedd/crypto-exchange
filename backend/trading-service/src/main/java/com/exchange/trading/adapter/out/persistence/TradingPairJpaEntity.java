package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.domain.model.TradingPair;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trading_pairs", schema = "market")
class TradingPairJpaEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 32)
    private String symbol;

    String getSymbol() {
        return symbol;
    }

    @Column(name = "base_currency_id", nullable = false)
    private Long baseCurrencyId;

    @Column(name = "quote_currency_id", nullable = false)
    private Long quoteCurrencyId;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    TradingPair toDomain() {
        TradingPair pair = new TradingPair();
        pair.setId(id);
        pair.setSymbol(symbol);
        pair.setBaseCurrencyId(baseCurrencyId);
        pair.setQuoteCurrencyId(quoteCurrencyId);
        pair.setActive(active);
        return pair;
    }
}
