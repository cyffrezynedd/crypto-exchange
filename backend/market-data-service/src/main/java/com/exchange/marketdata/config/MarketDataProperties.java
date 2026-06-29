package com.exchange.marketdata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "market-data")
public class MarketDataProperties {

    private Map<Long, String> pairSymbols = new HashMap<>();
    private int maxRecentTrades = 100;

    public Map<Long, String> getPairSymbols() {
        return pairSymbols;
    }

    public void setPairSymbols(Map<Long, String> pairSymbols) {
        this.pairSymbols = pairSymbols;
    }

    public int getMaxRecentTrades() {
        return maxRecentTrades;
    }

    public void setMaxRecentTrades(int maxRecentTrades) {
        this.maxRecentTrades = maxRecentTrades;
    }
}
