package com.exchange.trading.domain.model;

public class TradingPair {

    private Long id;
    private String symbol;
    private Long baseCurrencyId;
    private Long quoteCurrencyId;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(Long baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public Long getQuoteCurrencyId() {
        return quoteCurrencyId;
    }

    public void setQuoteCurrencyId(Long quoteCurrencyId) {
        this.quoteCurrencyId = quoteCurrencyId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
