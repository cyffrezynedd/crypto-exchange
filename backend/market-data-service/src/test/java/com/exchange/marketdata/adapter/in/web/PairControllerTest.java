package com.exchange.marketdata.adapter.in.web;

import com.exchange.common.web.PageResponse;
import com.exchange.marketdata.adapter.in.web.dto.OrderBookLevel;
import com.exchange.marketdata.adapter.in.web.dto.TradingPairResponse;
import com.exchange.marketdata.application.OrderBookService;
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

    @Mock
    private OrderBookService orderBookService;

    private PairController controller;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = ExchangeObjectMapper.create();
        controller = new PairController(store, orderBookService, objectMapper);
    }

    @Test
    void listPairsReturnsSortedSymbolsFromStore() {
        when(store.listPairs()).thenReturn(List.of("BTC_USDT", "ETH_USDT"));

        List<TradingPairResponse> pairs = controller.listPairs();

        assertEquals(2, pairs.size());
        assertEquals("BTC_USDT", pairs.get(0).symbol());
    }

    @Test
    void getOrderBookReturnsPagedLevels() {
        PageResponse<OrderBookLevel> page = new PageResponse<>(
                List.of(new OrderBookLevel("o1", "59000", "0.5", "SELL", "alice")),
                0,
                10,
                1,
                1
        );
        when(orderBookService.getLevels("BTC_USDT", null, null, 0, 10)).thenReturn(page);

        PageResponse<OrderBookLevel> book = controller.getOrderBook("BTC_USDT", null, null, 0, 10);

        assertEquals(1, book.content().size());
        assertEquals("alice", book.content().getFirst().username());
    }
}
