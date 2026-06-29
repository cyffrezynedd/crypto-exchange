package com.exchange.common.event;

import com.exchange.common.json.ExchangeObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventSerializationTest {

    private final ObjectMapper mapper = ExchangeObjectMapper.create();

    @Test
    void roundTripsOrderCreatedEvent() throws Exception {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                42L,
                1L,
                "client-1",
                "BUY",
                "LIMIT",
                "60000.00",
                "0.5",
                "NEW",
                Instant.parse("2026-06-25T12:00:00Z")
        );

        String json = mapper.writeValueAsString(event);
        OrderCreatedEvent restored = mapper.readValue(json, OrderCreatedEvent.class);

        assertEquals(event.orderId(), restored.orderId());
        assertEquals("BUY", restored.side());
        assertTrue(json.contains("60000.00"));
    }

    @Test
    void roundTripsTradeExecutedEvent() throws Exception {
        TradeExecutedEvent event = new TradeExecutedEvent(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                1L,
                UUID.fromString("550e8400-e29b-41d4-a716-446655440002"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440003"),
                10L,
                11L,
                UUID.fromString("550e8400-e29b-41d4-a716-446655440002"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440003"),
                "60000.00",
                "0.25",
                Instant.parse("2026-06-25T12:30:00Z")
        );

        String json = mapper.writeValueAsString(event);
        TradeExecutedEvent restored = mapper.readValue(json, TradeExecutedEvent.class);

        assertEquals(event.tradeId(), restored.tradeId());
        assertEquals("0.25", restored.quantity());
    }

    @Test
    void roundTripsOutboxEnvelope() throws Exception {
        OrderCreatedEvent payload = new OrderCreatedEvent(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                1L,
                1L,
                "client-1",
                "SELL",
                "LIMIT",
                "50000.00",
                "1.0",
                "NEW",
                Instant.parse("2026-06-25T12:00:00Z")
        );
        OutboxEnvelope<OrderCreatedEvent> envelope = new OutboxEnvelope<>(
                UUID.fromString("660e8400-e29b-41d4-a716-446655440000"),
                EventType.ORDER_CREATED,
                AggregateType.ORDER,
                payload.orderId().toString(),
                Instant.parse("2026-06-25T12:00:01Z"),
                payload
        );

        String json = mapper.writeValueAsString(envelope);
        OutboxEnvelope<?> restored = mapper.readValue(json, OutboxEnvelope.class);

        assertEquals(EventType.ORDER_CREATED, restored.eventType());
        assertEquals(AggregateType.ORDER, restored.aggregateType());
    }
}
