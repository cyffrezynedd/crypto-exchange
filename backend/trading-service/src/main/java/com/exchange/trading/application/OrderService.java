package com.exchange.trading.application;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.trading.domain.exception.DuplicateClientOrderException;
import com.exchange.trading.domain.exception.OrderAccessDeniedException;
import com.exchange.trading.domain.exception.OrderNotFoundException;
import com.exchange.trading.domain.exception.TradingPairNotFoundException;
import com.exchange.trading.domain.model.MatchResult;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderStatus;
import com.exchange.trading.domain.model.OrderType;
import com.exchange.trading.domain.model.TradingPair;
import com.exchange.common.web.PageResponse;
import com.exchange.trading.port.in.CancelOrderCommand;
import com.exchange.trading.port.in.OrderSearchQuery;
import com.exchange.trading.port.in.OrderUseCase;
import com.exchange.trading.port.in.PlaceOrderCommand;
import com.exchange.trading.port.out.ClearingPort;
import com.exchange.trading.port.out.MatchingEnginePort;
import com.exchange.trading.port.out.OrderRepositoryPort;
import com.exchange.trading.port.out.OutboxPort;
import com.exchange.trading.port.out.TradingPairRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService implements OrderUseCase {

    private static final int MONEY_SCALE = 18;

    private final OrderRepositoryPort orderRepository;
    private final TradingPairRepositoryPort tradingPairRepository;
    private final ClearingPort clearingPort;
    private final MatchingEnginePort matchingEngine;
    private final OutboxPort outboxPort;
    private final TradeExecutionService tradeExecutionService;

    public OrderService(
            OrderRepositoryPort orderRepository,
            TradingPairRepositoryPort tradingPairRepository,
            ClearingPort clearingPort,
            MatchingEnginePort matchingEngine,
            OutboxPort outboxPort,
            TradeExecutionService tradeExecutionService
    ) {
        this.orderRepository = orderRepository;
        this.tradingPairRepository = tradingPairRepository;
        this.clearingPort = clearingPort;
        this.matchingEngine = matchingEngine;
        this.outboxPort = outboxPort;
        this.tradeExecutionService = tradeExecutionService;
    }

    @Override
    @Transactional
    public Order placeOrder(PlaceOrderCommand command) {
        TradingPair pair = tradingPairRepository.findById(command.tradingPairId())
                .orElseThrow(() -> new TradingPairNotFoundException(command.tradingPairId()));

        if (!pair.isActive()) {
            throw new ExchangeException(ErrorCode.BUSINESS_RULE_VIOLATED, "Trading pair is not active");
        }

        validatePlaceOrder(command);

        if (command.clientOrderId() != null && !command.clientOrderId().isBlank()
                && orderRepository.existsByUserIdAndClientOrderId(command.userId(), command.clientOrderId())) {
            throw new DuplicateClientOrderException(command.clientOrderId());
        }

        Order order = buildOrder(command);
        BigDecimal freezeAmount = resolveFreezeAmount(order, pair);
        Long currencyId = resolveFreezeCurrencyId(order, pair);
        order.setLockedAmount(freezeAmount);

        Order saved = orderRepository.save(order);
        try {
            clearingPort.freezeFunds(saved.getId(), command.userId(), currencyId, freezeAmount);
        } catch (RuntimeException ex) {
            orderRepository.deleteById(saved.getId());
            throw ex;
        }

        outboxPort.saveOrderCreated(saved, command.username());

        List<MatchResult> matches = matchingEngine.submitOrder(saved);
        tradeExecutionService.executeMatches(matches);

        return orderRepository.findById(saved.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public Order cancelOrder(CancelOrderCommand command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (!order.getUserId().equals(command.userId())) {
            throw new OrderAccessDeniedException();
        }

        if (!order.isActive()) {
            throw new ExchangeException(ErrorCode.BUSINESS_RULE_VIOLATED, "Order cannot be cancelled in status: " + order.getStatus());
        }

        TradingPair pair = tradingPairRepository.findById(order.getTradingPairId())
                .orElseThrow(() -> new TradingPairNotFoundException(order.getTradingPairId()));

        matchingEngine.removeOrder(order.getId(), order.getTradingPairId());

        BigDecimal unfreezeAmount = resolveUnfreezeAmount(order);
        Long currencyId = resolveFreezeCurrencyId(order, pair);
        clearingPort.unfreezeFunds(order.getId(), order.getUserId(), currencyId, unfreezeAmount);

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());
        Order saved = orderRepository.save(order);
        outboxPort.saveOrderCancelled(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(Long userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (!order.getUserId().equals(userId)) {
            throw new OrderAccessDeniedException();
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> listOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Order> searchOrders(OrderSearchQuery query) {
        int page = Math.max(query.page(), 0);
        int size = Math.min(Math.max(query.size(), 1), 100);
        return orderRepository.search(
                query.userId(),
                query.side(),
                query.status(),
                query.tradingPairId(),
                page,
                size
        );
    }

    private void validatePlaceOrder(PlaceOrderCommand command) {
        if (command.quantity() == null || command.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExchangeException(ErrorCode.VALIDATION_FAILED, "Quantity must be positive");
        }
        if (command.type() == OrderType.LIMIT) {
            if (command.price() == null || command.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExchangeException(ErrorCode.VALIDATION_FAILED, "Limit order requires positive price");
            }
        } else if (command.price() != null) {
            throw new ExchangeException(ErrorCode.VALIDATION_FAILED, "Market order must not have price");
        }
    }

    private Order buildOrder(PlaceOrderCommand command) {
        Instant now = Instant.now();
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUserId(command.userId());
        order.setTradingPairId(command.tradingPairId());
        order.setClientOrderId(command.clientOrderId());
        order.setSide(command.side());
        order.setType(command.type());
        order.setPrice(command.type() == OrderType.LIMIT ? command.price() : null);
        order.setQuantity(command.quantity());
        order.setFilledQuantity(BigDecimal.ZERO);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        return order;
    }

    private BigDecimal resolveFreezeAmount(Order order, TradingPair pair) {
        if (order.getSide() == OrderSide.SELL) {
            return order.getQuantity();
        }
        if (order.getType() == OrderType.LIMIT) {
            return order.getPrice().multiply(order.getQuantity());
        }
        BigDecimal bestAsk = matchingEngine.getBestAskPrice(order.getTradingPairId())
                .orElseThrow(() -> new ExchangeException(
                        ErrorCode.BUSINESS_RULE_VIOLATED,
                        "No liquidity available for market buy order"
                ));
        return bestAsk.multiply(order.getQuantity());
    }

    private BigDecimal resolveUnfreezeAmount(Order order) {
        BigDecimal remaining = order.remainingQuantity();
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (order.getSide() == OrderSide.SELL) {
            return remaining;
        }
        if (order.getLockedAmount() != null) {
            return order.getLockedAmount()
                    .multiply(remaining)
                    .divide(order.getQuantity(), MONEY_SCALE, RoundingMode.HALF_UP);
        }
        if (order.getType() == OrderType.LIMIT) {
            return remaining.multiply(order.getPrice());
        }
        return BigDecimal.ZERO;
    }

    private Long resolveFreezeCurrencyId(Order order, TradingPair pair) {
        return order.getSide() == OrderSide.SELL
                ? pair.getBaseCurrencyId()
                : pair.getQuoteCurrencyId();
    }
}
