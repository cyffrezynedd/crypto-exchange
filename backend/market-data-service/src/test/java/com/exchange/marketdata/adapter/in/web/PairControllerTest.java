package com.exchange.marketdata.adapter.in.web;

import com.exchange.marketdata.adapter.in.web.dto.OrderBookResponse;
import com.exchange.marketdata.adapter.in.web.dto.TradingPairResponse;
import com.exchange.marketdata.redis.RedisMarketDataStore;
import com.exchange.common.json.ExchangeObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PairControllerTest {

    @Mock
    private RedisMarketDataStore store;

    private PairController controller;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = ExchangeObjectMapper.create();
        controller = new PairController(store, objectMapper);
    }

    @Test
    void listPairsReturnsSortedSymbolsFromStore() {
        when(store.listPairs()).thenReturn(List.of("BTC_USDT", "ETH_USDT"));

        List<TradingPairResponse> pairs = controller.listPairs();

        assertEquals(2, pairs.size());
        assertEquals("BTC_USDT", pairs.get(0).symbol());
    }

    @Test
    void getOrderBookMapsBidsAndAsks() {
        when(store.getBids("BTC_USDT")).thenReturn(List.of(
                new RedisMarketDataStore.OrderBookEntry("o1", "59000", "0.5")));
        when(store.getAsks("BTC_USDT")).thenReturn(List.of(
                new RedisMarketDataStore.OrderBookEntry("o2", "60000", "1")));

        OrderBookResponse book = controller.getOrderBook("BTC_USDT");

        assertEquals("BTC_USDT", book.symbol());
        assertEquals(1, book.bids().size());
        assertEquals("59000", book.bids().getFirst().price());
        assertEquals(1, book.asks().size());
    }
}
