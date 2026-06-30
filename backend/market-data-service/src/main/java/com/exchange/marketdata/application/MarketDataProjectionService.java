package com.exchange.marketdata.application;

import com.exchange.common.event.OrderCreatedEvent;
import com.exchange.common.event.TradeExecutedEvent;
import com.exchange.marketdata.config.MarketDataProperties;
import com.exchange.marketdata.redis.RedisMarketDataStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MarketDataProjectionService {

    private static final Logger log = LoggerFactory.getLogger(MarketDataProjectionService.class);

    private final RedisMarketDataStore store;
    private final MarketDataProperties properties;

    public MarketDataProjectionService(RedisMarketDataStore store, MarketDataProperties properties) {
        this.store = store;
        this.properties = properties;
    }

    @PostConstruct
    void seedConfiguredPairs() {
        store.seedPairSymbols(properties.getPairSymbols());
    }

    public void onOrderCreated(OrderCreatedEvent event) {
        String symbol = store.resolveSymbol(event.tradingPairId());
        store.indexOrderOwner(event.orderId(), event.userId(), event.username());
        store.addOrder(symbol, event.side(), event.orderId(), event.price(), event.quantity(), event.username());
        log.debug("Order book updated for {} order {}", symbol, event.orderId());
    }

    public void onTradeExecuted(TradeExecutedEvent event) throws JsonProcessingException {
        String symbol = store.resolveSymbol(event.tradingPairId());
        store.recordTrade(
                symbol,
                event.tradeId(),
                event.price(),
                event.quantity(),
                event.executedAt().toString(),
                event.makerOrderId(),
                event.takerOrderId()
        );
        log.debug("Trade recorded for {} trade {}", symbol, event.tradeId());
    }
}
