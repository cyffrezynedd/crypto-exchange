package com.exchange.clearing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clearing.features")
public class ClearingFeaturesProperties {

    private boolean depositEnabled;

    public boolean isDepositEnabled() {
        return depositEnabled;
    }

    public void setDepositEnabled(boolean depositEnabled) {
        this.depositEnabled = depositEnabled;
    }
}
