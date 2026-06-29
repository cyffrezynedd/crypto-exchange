package com.exchange.trading.adapter.out.persistence;

import com.exchange.trading.adapter.out.kafka.KafkaEventPublisher;
import com.exchange.trading.config.TradingProperties;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxWorker {

    private static final Logger log = LoggerFactory.getLogger(OutboxWorker.class);

    private final OutboxEventJpaRepository outboxRepository;
    private final KafkaEventPublisher eventPublisher;
    private final TradingProperties tradingProperties;

    public OutboxWorker(
            OutboxEventJpaRepository outboxRepository,
            KafkaEventPublisher eventPublisher,
            TradingProperties tradingProperties
    ) {
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
        this.tradingProperties = tradingProperties;
    }

    @Scheduled(fixedDelay = 5000)
    @SchedulerLock(name = "outboxWorker", lockAtLeastFor = "4s", lockAtMostFor = "30s")
    @Transactional
    public void processOutbox() {
        List<OutboxEventJpaEntity> events = outboxRepository.findUnprocessed();
        int processed = 0;
        for (OutboxEventJpaEntity event : events) {
            if (processed >= tradingProperties.getOutbox().getBatchSize()) {
                break;
            }
            try {
                eventPublisher.publish(
                        tradingProperties.getOutbox().getTopic(),
                        event.getAggregateId(),
                        event.getPayload()
                );
                event.setProcessed(true);
                event.setProcessedAt(Instant.now());
                event.setLastError(null);
                outboxRepository.save(event);
                processed++;
            } catch (Exception ex) {
                log.warn("Failed to publish outbox event {}: {}", event.getId(), ex.getMessage());
                event.setRetryCount(event.getRetryCount() + 1);
                event.setLastError(ex.getMessage());
                outboxRepository.save(event);
            }
        }
    }
}
