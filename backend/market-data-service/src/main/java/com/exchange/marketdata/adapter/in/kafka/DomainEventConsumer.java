package com.exchange.marketdata.adapter.in.kafka;

import com.exchange.common.event.EventType;
import com.exchange.common.event.OrderCreatedEvent;
import com.exchange.common.event.TradeExecutedEvent;
import com.exchange.marketdata.application.MarketDataProjectionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DomainEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final MarketDataProjectionService projectionService;

    public DomainEventConsumer(ObjectMapper objectMapper, MarketDataProjectionService projectionService) {
        this.objectMapper = objectMapper;
        this.projectionService = projectionService;
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic:exchange.domain.events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(message);
        String eventType = root.path("eventType").asText();

        switch (eventType) {
            case EventType.ORDER_CREATED -> handleOrderCreated(root);
            case EventType.TRADE_EXECUTED -> handleTradeExecuted(root);
            default -> log.trace("Ignoring event type {}", eventType);
        }
    }

    private void handleOrderCreated(JsonNode root) throws JsonProcessingException {
        OrderCreatedEvent event = objectMapper.treeToValue(root.path("payload"), OrderCreatedEvent.class);
        projectionService.onOrderCreated(event);
    }

    private void handleTradeExecuted(JsonNode root) throws JsonProcessingException {
        TradeExecutedEvent event = objectMapper.treeToValue(root.path("payload"), TradeExecutedEvent.class);
        projectionService.onTradeExecuted(event);
    }
}
