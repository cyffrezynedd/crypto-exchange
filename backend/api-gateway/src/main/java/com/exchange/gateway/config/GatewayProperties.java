package com.exchange.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    private Jwt jwt = new Jwt();
    private Services services = new Services();
    private Discovery discovery = new Discovery();
    private List<String> publicPaths = new ArrayList<>();
    private String internalSecret = "";

    public String getInternalSecret() {
        return internalSecret;
    }

    public void setInternalSecret(String internalSecret) {
        this.internalSecret = internalSecret;
    }

    public Discovery getDiscovery() {
        return discovery;
    }

    public void setDiscovery(Discovery discovery) {
        this.discovery = discovery;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public static class Jwt {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Services {
        private String iamUri = "http://localhost:8081";
        private String clearingUri = "http://localhost:8082";
        private String tradingUri = "http://localhost:8083";
        private String marketUri = "http://localhost:8084";

        public String getIamUri() {
            return iamUri;
        }

        public void setIamUri(String iamUri) {
            this.iamUri = iamUri;
        }

        public String getClearingUri() {
            return clearingUri;
        }

        public void setClearingUri(String clearingUri) {
            this.clearingUri = clearingUri;
        }

        public String getTradingUri() {
            return tradingUri;
        }

        public void setTradingUri(String tradingUri) {
            this.tradingUri = tradingUri;
        }

        public String getMarketUri() {
            return marketUri;
        }

        public void setMarketUri(String marketUri) {
            this.marketUri = marketUri;
        }
    }

    public static class Discovery {
        private boolean consulEnabled = false;

        public boolean isConsulEnabled() {
            return consulEnabled;
        }

        public void setConsulEnabled(boolean consulEnabled) {
            this.consulEnabled = consulEnabled;
        }
    }
}

