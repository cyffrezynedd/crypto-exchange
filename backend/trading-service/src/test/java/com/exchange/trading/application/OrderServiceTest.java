package com.exchange.trading.application;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.trading.domain.exception.DuplicateClientOrderException;
import com.exchange.trading.domain.exception.OrderAccessDeniedException;
import com.exchange.trading.domain.exception.TradingPairNotFoundException;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.domain.model.OrderType;
import com.exchange.trading.domain.model.TradingPair;
import com.exchange.trading.port.in.CancelOrderCommand;
import com.exchange.trading.port.in.PlaceOrderCommand;
import com.exchange.trading.port.out.ClearingPort;
import com.exchange.trading.port.out.MatchingEnginePort;
import com.exchange.trading.port.out.OrderRepositoryPort;
import com.exchange.trading.port.out.OutboxPort;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private TradingPairRepositoryPort tradingPairRepository;

    @Mock
    private ClearingPort clearingPort;

    @Mock
    private MatchingEnginePort matchingEngine;

    @Mock
    private OutboxPort outboxPort;

    @Mock
    private TradeExecutionService tradeExecutionService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                tradingPairRepository,
                clearingPort,
                matchingEngine,
                outboxPort,
                tradeExecutionService);
    }

    @Test
    void placeLimitBuyOrderFreezesQuoteAndPersists() {
        TradingPair pair = activePair(1L, 10L, 20L);
        when(tradingPairRepository.findById(1L)).thenReturn(Optional.of(pair));
        when(orderRepository.existsByUserIdAndClientOrderId(5L, "c1")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(matchingEngine.submitOrder(any(Order.class))).thenReturn(List.of());
        when(orderRepository.findById(any(UUID.class))).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            Order order = new Order();
            order.setId(id);
            order.setStatus(OrderStatus.NEW);
            return Optional.of(order);
        });

        PlaceOrderCommand command = new PlaceOrderCommand(
                5L, 1L, "c1", OrderSide.BUY, OrderType.LIMIT, new BigDecimal("100"), new BigDecimal("2"), "trader");

        Order placed = orderService.placeOrder(command);

        verify(clearingPort).freezeFunds(any(UUID.class), eq(5L), eq(20L), eq(new BigDecimal("200")));
        verify(outboxPort).saveOrderCreated(any(Order.class), eq("trader"));
        assertEquals(OrderStatus.NEW, placed.getStatus());
    }

    @Test
    void placeOrderRejectsInactivePair() {
        TradingPair pair = activePair(2L, 1L, 2L);
        pair.setActive(false);
        when(tradingPairRepository.findById(2L)).thenReturn(Optional.of(pair));

        ExchangeException ex = assertThrows(ExchangeException.class, () -> orderService.placeOrder(
                new PlaceOrderCommand(1L, 2L, null, OrderSide.BUY, OrderType.LIMIT,
                        new BigDecimal("10"), new BigDecimal("1"), null)));
        assertEquals(ErrorCode.BUSINESS_RULE_VIOLATED, ex.code());
    }

    @Test
    void placeOrderRejectsDuplicateClientOrderId() {
        TradingPair pair = activePair(1L, 1L, 2L);
        when(tradingPairRepository.findById(1L)).thenReturn(Optional.of(pair));
        when(orderRepository.existsByUserIdAndClientOrderId(3L, "dup")).thenReturn(true);

        assertThrows(DuplicateClientOrderException.class, () -> orderService.placeOrder(
                new PlaceOrderCommand(3L, 1L, "dup", OrderSide.SELL, OrderType.LIMIT,
                        new BigDecimal("50"), new BigDecimal("1"), null)));
        verify(clearingPort, never()).freezeFunds(any(), any(), any(), any());
    }

    @Test
    void cancelOrderUnfreezesAndMarksCancelled() {
        UUID orderId = UUID.randomUUID();
        TradingPair pair = activePair(1L, 10L, 20L);
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(8L);
        order.setTradingPairId(1L);
        order.setSide(OrderSide.BUY);
        order.setType(OrderType.LIMIT);
        order.setPrice(new BigDecimal("100"));
        order.setQuantity(new BigDecimal("2"));
        order.setFilledQuantity(BigDecimal.ZERO);
        order.setLockedAmount(new BigDecimal("200"));
        order.setStatus(OrderStatus.NEW);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(tradingPairRepository.findById(1L)).thenReturn(Optional.of(pair));
        when(orderRepository.save(order)).thenReturn(order);

        Order cancelled = orderService.cancelOrder(new CancelOrderCommand(8L, orderId));

        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        verify(clearingPort).unfreezeFunds(eq(orderId), eq(8L), eq(20L), argThat(amount ->
                amount.compareTo(new BigDecimal("200")) == 0));
        verify(matchingEngine).removeOrder(orderId, 1L);
        verify(outboxPort).saveOrderCancelled(order);
    }

    @Test
    void getOrderDeniesForeignUser() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(1L);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(OrderAccessDeniedException.class, () -> orderService.getOrder(2L, orderId));
    }

    @Test
    void placeOrderFailsWhenPairMissing() {
        when(tradingPairRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(TradingPairNotFoundException.class, () -> orderService.placeOrder(
                new PlaceOrderCommand(1L, 404L, null, OrderSide.BUY, OrderType.LIMIT,
                        new BigDecimal("1"), new BigDecimal("1"), null)));
    }

    private static TradingPair activePair(long id, long baseCurrencyId, long quoteCurrencyId) {
        TradingPair pair = new TradingPair();
        pair.setId(id);
        pair.setBaseCurrencyId(baseCurrencyId);
        pair.setQuoteCurrencyId(quoteCurrencyId);
        pair.setActive(true);
        pair.setSymbol("BTC/USDT");
        return pair;
    }
}
