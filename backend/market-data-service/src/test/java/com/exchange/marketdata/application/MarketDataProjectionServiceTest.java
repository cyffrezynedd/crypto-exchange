package com.exchange.marketdata.application;

import com.exchange.common.event.OrderCreatedEvent;
import com.exchange.common.event.TradeExecutedEvent;
import com.exchange.marketdata.config.MarketDataProperties;
import com.exchange.marketdata.redis.RedisMarketDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketDataProjectionServiceTest {

    @Mock
    private RedisMarketDataStore store;

    private MarketDataProjectionService service;

    @BeforeEach
    void setUp() {
        MarketDataProperties properties = new MarketDataProperties();
        properties.setPairSymbols(Map.of(1L, "BTC_USDT", 2L, "ETH_USDT"));
        service = new MarketDataProjectionService(store, properties);
        service.seedConfiguredPairs();
    }

    @Test
    void seedConfiguredPairsWritesSymbolsToStore() {
        verify(store).seedPairSymbols(Map.of(1L, "BTC_USDT", 2L, "ETH_USDT"));
    }

    @Test
    void onOrderCreatedAddsOrderToBook() {
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId, 10L, 1L, "c1", "BUY", "LIMIT",
                "60000", "0.5", "NEW", Instant.parse("2026-06-25T12:00:00Z"), "trader");
        when(store.resolveSymbol(1L)).thenReturn("BTC_USDT");

        service.onOrderCreated(event);

        verify(store).addOrder("BTC_USDT", "BUY", orderId, "60000", "0.5", "trader");
        verify(store).indexOrderOwner(orderId, 10L, "trader");
    }

    @Test
    void onTradeExecutedRecordsTrade() throws Exception {
        UUID tradeId = UUID.randomUUID();
        UUID makerId = UUID.randomUUID();
        UUID takerId = UUID.randomUUID();
        TradeExecutedEvent event = new TradeExecutedEvent(
                tradeId, 1L, makerId, takerId, 1L, 2L,
                makerId, takerId, "60000", "0.25",
                Instant.parse("2026-06-25T12:30:00Z"));
        when(store.resolveSymbol(1L)).thenReturn("BTC_USDT");

        service.onTradeExecuted(event);

        verify(store).recordTrade(
                "BTC_USDT",
                tradeId,
                "60000",
                "0.25",
                "2026-06-25T12:30:00Z",
                makerId,
                takerId);
    }
}
