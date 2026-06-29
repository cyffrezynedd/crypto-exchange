package com.exchange.trading.application;

import com.exchange.trading.domain.model.MatchResult;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.domain.model.OrderType;
import com.exchange.trading.domain.model.Trade;
import com.exchange.trading.domain.model.TradingPair;
import com.exchange.trading.port.out.ClearingPort;
import com.exchange.trading.port.out.OrderRepositoryPort;
import com.exchange.trading.port.out.OutboxPort;
import com.exchange.trading.port.out.TradeRepositoryPort;
import com.exchange.trading.port.out.TradingPairRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeExecutionServiceTest {

    @Mock
    private TradeRepositoryPort tradeRepository;
    @Mock
    private OrderRepositoryPort orderRepository;
    @Mock
    private TradingPairRepositoryPort tradingPairRepository;
    @Mock
    private ClearingPort clearingPort;
    @Mock
    private OutboxPort outboxPort;

    private TradeExecutionService service;

    @BeforeEach
    void setUp() {
        service = new TradeExecutionService(
                tradeRepository,
                orderRepository,
                tradingPairRepository,
                clearingPort,
                outboxPort);
    }

    @Test
    void executeMatchesSettlesTradeInClearing() {
        TradingPair pair = new TradingPair();
        pair.setId(1L);
        pair.setBaseCurrencyId(10L);
        pair.setQuoteCurrencyId(20L);

        Order buy = limitOrder(OrderSide.BUY, 1L);
        Order sell = limitOrder(OrderSide.SELL, 2L);
        MatchResult match = new MatchResult(buy, sell, new BigDecimal("50000"), new BigDecimal("0.5"));

        when(tradingPairRepository.findById(1L)).thenReturn(Optional.of(pair));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.executeMatches(List.of(match));

        verify(clearingPort).settleTrade(any(Trade.class), eq(pair));
        verify(outboxPort).saveTradeExecuted(any(Trade.class));
    }

    private static Order limitOrder(OrderSide side, long userId) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(userId);
        order.setTradingPairId(1L);
        order.setSide(side);
        order.setType(OrderType.LIMIT);
        order.setPrice(new BigDecimal("50000"));
        order.setQuantity(new BigDecimal("1"));
        order.setFilledQuantity(BigDecimal.ZERO);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(Instant.now());
        return order;
    }
}
