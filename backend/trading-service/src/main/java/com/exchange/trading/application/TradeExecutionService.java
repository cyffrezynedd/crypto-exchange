package com.exchange.trading.application;

import com.exchange.trading.domain.model.MatchResult;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.Trade;
import com.exchange.trading.domain.model.TradingPair;
import com.exchange.trading.port.out.ClearingPort;
import com.exchange.trading.port.out.OrderRepositoryPort;
import com.exchange.trading.port.out.OutboxPort;
import com.exchange.trading.port.out.TradeRepositoryPort;
import com.exchange.trading.port.out.TradingPairRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TradeExecutionService {

    private final TradeRepositoryPort tradeRepository;
    private final OrderRepositoryPort orderRepository;
    private final TradingPairRepositoryPort tradingPairRepository;
    private final ClearingPort clearingPort;
    private final OutboxPort outboxPort;

    public TradeExecutionService(
            TradeRepositoryPort tradeRepository,
            OrderRepositoryPort orderRepository,
            TradingPairRepositoryPort tradingPairRepository,
            ClearingPort clearingPort,
            OutboxPort outboxPort
    ) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
        this.tradingPairRepository = tradingPairRepository;
        this.clearingPort = clearingPort;
        this.outboxPort = outboxPort;
    }

    @Transactional
    public void executeMatches(List<MatchResult> matches) {
        for (MatchResult match : matches) {
            Order buyOrder = match.takerOrder().getSide() == OrderSide.BUY
                    ? match.takerOrder()
                    : match.makerOrder();
            Order sellOrder = match.takerOrder().getSide() == OrderSide.SELL
                    ? match.takerOrder()
                    : match.makerOrder();

            buyOrder.applyFill(match.quantity());
            sellOrder.applyFill(match.quantity());
            buyOrder.setUpdatedAt(Instant.now());
            sellOrder.setUpdatedAt(Instant.now());

            Trade trade = new Trade();
            trade.setId(UUID.randomUUID());
            trade.setTradingPairId(buyOrder.getTradingPairId());
            trade.setBuyOrderId(buyOrder.getId());
            trade.setSellOrderId(sellOrder.getId());
            trade.setBuyerId(buyOrder.getUserId());
            trade.setSellerId(sellOrder.getUserId());
            trade.setMakerOrderId(match.makerOrder().getId());
            trade.setTakerOrderId(match.takerOrder().getId());
            trade.setPrice(match.price());
            trade.setQuantity(match.quantity());
            trade.setExecutedAt(Instant.now());

            TradingPair pair = tradingPairRepository.findById(trade.getTradingPairId())
                    .orElseThrow();

            tradeRepository.save(trade);
            orderRepository.save(buyOrder);
            orderRepository.save(sellOrder);
            clearingPort.settleTrade(trade, pair);
            outboxPort.saveTradeExecuted(trade);
        }
    }
}
