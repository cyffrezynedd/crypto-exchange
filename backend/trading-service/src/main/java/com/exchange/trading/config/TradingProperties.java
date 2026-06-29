package com.exchange.trading.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trading")
public class TradingProperties {

    private final Outbox outbox = new Outbox();

    public Outbox getOutbox() {
        return outbox;
    }

    public static class Outbox {

        private String topic = "exchange.domain.events";
        private int batchSize = 100;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }
    }
}
