package com.exchange.marketdata.adapter.in.kafka;

import com.exchange.common.event.EventType;
import com.exchange.common.event.OrderCreatedEvent;
import com.exchange.common.event.TradeExecutedEvent;
import com.exchange.common.json.ExchangeObjectMapper;
import com.exchange.marketdata.application.MarketDataProjectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DomainEventConsumerTest {

    @Mock
    private MarketDataProjectionService projectionService;

    private ObjectMapper objectMapper;
    private DomainEventConsumer consumer;

    @BeforeEach
    void setUp() {
        objectMapper = ExchangeObjectMapper.create();
        consumer = new DomainEventConsumer(objectMapper, projectionService);
    }

    @Test
    void consumesOrderCreatedEvent() throws Exception {
        OrderCreatedEvent payload = new OrderCreatedEvent(
                UUID.randomUUID(), 1L, 1L, "c1", "SELL", "LIMIT",
                "50000", "1", "NEW", Instant.parse("2026-06-25T10:00:00Z"));
        String message = envelope(EventType.ORDER_CREATED, payload);

        consumer.consume(message);

        verify(projectionService).onOrderCreated(payload);
    }

    @Test
    void consumesTradeExecutedEvent() throws Exception {
        TradeExecutedEvent payload = new TradeExecutedEvent(
                UUID.randomUUID(), 1L, UUID.randomUUID(), UUID.randomUUID(),
                1L, 2L, UUID.randomUUID(), UUID.randomUUID(),
                "50000", "0.1", Instant.parse("2026-06-25T11:00:00Z"));
        String message = envelope(EventType.TRADE_EXECUTED, payload);

        consumer.consume(message);

        verify(projectionService).onTradeExecuted(payload);
    }

    @Test
    void ignoresUnknownEventType() throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("eventType", "SomethingElse");
        root.set("payload", objectMapper.createObjectNode());

        consumer.consume(objectMapper.writeValueAsString(root));

        verifyNoInteractions(projectionService);
    }

    private String envelope(String eventType, Object payload) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("eventType", eventType);
        root.set("payload", objectMapper.valueToTree(payload));
        return objectMapper.writeValueAsString(root);
    }
}
