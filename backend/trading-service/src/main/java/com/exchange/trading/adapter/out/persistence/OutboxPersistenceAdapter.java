package com.exchange.trading.adapter.out.persistence;

import com.exchange.common.event.AggregateType;
import com.exchange.common.event.EventType;
import com.exchange.common.event.OrderCreatedEvent;
import com.exchange.common.event.OutboxEnvelope;
import com.exchange.common.event.TradeExecutedEvent;
import com.exchange.common.json.ExchangeObjectMapper;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.Trade;
import com.exchange.trading.port.out.OutboxPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
class OutboxPersistenceAdapter implements OutboxPort {

    private final OutboxEventJpaRepository repository;

    OutboxPersistenceAdapter(OutboxEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveOrderCreated(Order order) {
        OrderCreatedEvent payload = new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getTradingPairId(),
                order.getClientOrderId(),
                order.getSide().name(),
                order.getType().name(),
                decimalToString(order.getPrice()),
                order.getQuantity().toPlainString(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
        saveEvent(
                AggregateType.ORDER,
                order.getId().toString(),
                EventType.ORDER_CREATED,
                payload,
                order.getCreatedAt()
        );
    }

    @Override
    public void saveOrderCancelled(Order order) {
        Map<String, Object> payload = Map.of(
                "orderId", order.getId().toString(),
                "userId", order.getUserId(),
                "tradingPairId", order.getTradingPairId(),
                "status", order.getStatus().name(),
                "cancelledAt", order.getUpdatedAt().toString()
        );
        saveEvent(
                AggregateType.ORDER,
                order.getId().toString(),
                EventType.ORDER_CANCELLED,
                payload,
                order.getUpdatedAt()
        );
    }

    @Override
    public void saveTradeExecuted(Trade trade) {
        TradeExecutedEvent payload = new TradeExecutedEvent(
                trade.getId(),
                trade.getTradingPairId(),
                trade.getBuyOrderId(),
                trade.getSellOrderId(),
                trade.getBuyerId(),
                trade.getSellerId(),
                trade.getMakerOrderId(),
                trade.getTakerOrderId(),
                trade.getPrice().toPlainString(),
                trade.getQuantity().toPlainString(),
                trade.getExecutedAt()
        );
        saveEvent(
                AggregateType.TRADE,
                trade.getId().toString(),
                EventType.TRADE_EXECUTED,
                payload,
                trade.getExecutedAt()
        );
    }

    private void saveEvent(String aggregateType, String aggregateId, String eventType, Object payload, Instant occurredAt) {
        UUID eventId = UUID.randomUUID();
        OutboxEnvelope<Object> envelope = new OutboxEnvelope<>(
                eventId,
                eventType,
                aggregateType,
                aggregateId,
                occurredAt,
                payload
        );

        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.setId(eventId);
        entity.setAggregateType(aggregateType);
        entity.setAggregateId(aggregateId);
        entity.setEventType(eventType);
        entity.setPayload(toJson(envelope));
        entity.setProcessed(false);
        entity.setRetryCount(0);
        entity.setCreatedAt(occurredAt);
        repository.save(entity);
    }

    private static String decimalToString(java.math.BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }

    private static String toJson(Object value) {
        try {
            return ExchangeObjectMapper.get().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }
}
