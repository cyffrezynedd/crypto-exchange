package com.exchange.trading.adapter.out.matching;

import com.exchange.trading.domain.model.MatchResult;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.domain.model.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryMatchingEngineTest {

    private InMemoryMatchingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new InMemoryMatchingEngine();
    }

    @Test
    void limitBuyMatchesRestingSellAtAskPrice() {
        Order sell = limitOrder(OrderSide.SELL, "60000", "1.0");
        engine.submitOrder(sell);

        Order buy = limitOrder(OrderSide.BUY, "61000", "1.0");
        List<MatchResult> matches = engine.submitOrder(buy);

        assertEquals(1, matches.size());
        assertEquals(new BigDecimal("60000"), matches.getFirst().price());
        assertEquals(new BigDecimal("1.0"), matches.getFirst().quantity());
        assertEquals(OrderStatus.FILLED, buy.getStatus());
        assertEquals(OrderStatus.FILLED, sell.getStatus());
    }

    @Test
    void limitBuyDoesNotCrossWhenPriceTooLow() {
        Order sell = limitOrder(OrderSide.SELL, "60000", "1.0");
        engine.submitOrder(sell);

        Order buy = limitOrder(OrderSide.BUY, "59000", "1.0");
        List<MatchResult> matches = engine.submitOrder(buy);

        assertTrue(matches.isEmpty());
        assertEquals(OrderStatus.NEW, buy.getStatus());
        assertEquals(OrderStatus.NEW, sell.getStatus());
    }

    @Test
    void partialFillLeavesBothOrdersActive() {
        Order sell = limitOrder(OrderSide.SELL, "50000", "2.0");
        engine.submitOrder(sell);

        Order buy = limitOrder(OrderSide.BUY, "50000", "0.75");
        engine.submitOrder(buy);

        assertEquals(OrderStatus.FILLED, buy.getStatus());
        assertEquals(OrderStatus.PARTIALLY_FILLED, sell.getStatus());
        assertEquals(new BigDecimal("1.25"), sell.remainingQuantity());
    }

    @Test
    void removeOrderClearsBook() {
        Order sell = limitOrder(OrderSide.SELL, "45000", "1.0");
        engine.submitOrder(sell);
        engine.removeOrder(sell.getId(), sell.getTradingPairId());

        assertTrue(engine.getBestAskPrice(1L).isEmpty());
    }

    private static Order limitOrder(OrderSide side, String price, String quantity) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(1L);
        order.setTradingPairId(1L);
        order.setSide(side);
        order.setType(OrderType.LIMIT);
        order.setPrice(new BigDecimal(price));
        order.setQuantity(new BigDecimal(quantity));
        order.setFilledQuantity(BigDecimal.ZERO);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(Instant.parse("2026-06-25T10:00:00Z"));
        return order;
    }
}
