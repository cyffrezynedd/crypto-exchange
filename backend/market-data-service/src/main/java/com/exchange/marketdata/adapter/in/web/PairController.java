package com.exchange.marketdata.adapter.in.web;

import com.exchange.marketdata.adapter.in.web.dto.OrderBookLevel;
import com.exchange.marketdata.adapter.in.web.dto.OrderBookResponse;
import com.exchange.marketdata.adapter.in.web.dto.TradeResponse;
import com.exchange.marketdata.adapter.in.web.dto.TradingPairResponse;
import com.exchange.marketdata.redis.RedisMarketDataStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pairs")
public class PairController {

    private final RedisMarketDataStore store;
    private final ObjectMapper objectMapper;

    public PairController(RedisMarketDataStore store, ObjectMapper objectMapper) {
        this.store = store;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<TradingPairResponse> listPairs() {
        return store.listPairs().stream()
                .map(TradingPairResponse::new)
                .toList();
    }

    @GetMapping("/{symbol}/orderbook")
    public OrderBookResponse getOrderBook(@PathVariable String symbol) {
        List<OrderBookLevel> bids = store.getBids(symbol).stream()
                .map(level -> new OrderBookLevel(level.orderId(), level.price(), level.quantity()))
                .toList();
        List<OrderBookLevel> asks = store.getAsks(symbol).stream()
                .map(level -> new OrderBookLevel(level.orderId(), level.price(), level.quantity()))
                .toList();
        return new OrderBookResponse(symbol, bids, asks);
    }

    @GetMapping("/{symbol}/trades")
    public List<TradeResponse> getRecentTrades(@PathVariable String symbol) {
        return store.getRecentTrades(symbol).stream()
                .map(this::toTradeResponse)
                .toList();
    }

    private TradeResponse toTradeResponse(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return new TradeResponse(
                    node.path("tradeId").asText(),
                    node.path("price").asText(),
                    node.path("quantity").asText(),
                    node.path("executedAt").asText()
            );
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Invalid trade payload in Redis", ex);
        }
    }
}
