package com.exchange.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    private static final String IAM_SERVICE = "iam-service";
    private static final String CLEARING_SERVICE = "clearing-service";
    private static final String TRADING_SERVICE = "trading-service";
    private static final String MARKET_SERVICE = "market-data-service";

    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder, GatewayProperties properties) {
        GatewayProperties.Services services = properties.getServices();

        return builder.routes()
                .route("iam-service", r -> r
                        .path(GatewayApiPaths.pattern("iam"))
                        .filters(f -> f
                                .stripPrefix(GatewayApiPaths.ROUTE_STRIP_PARTS)
                                .circuitBreaker(config -> config
                                        .setName("iamService")
                                        .setFallbackUri("forward:/fallback/iam")))
                        .uri(target(properties, IAM_SERVICE, services.getIamUri())))
                .route("trading-service", r -> r
                        .path(GatewayApiPaths.pattern("trading"))
                        .filters(f -> f
                                .stripPrefix(GatewayApiPaths.ROUTE_STRIP_PARTS)
                                .circuitBreaker(config -> config
                                        .setName("tradingService")
                                        .setFallbackUri("forward:/fallback/trading")))
                        .uri(target(properties, TRADING_SERVICE, services.getTradingUri())))
                .route("clearing-service", r -> r
                        .path(GatewayApiPaths.pattern("clearing"))
                        .filters(f -> f
                                .stripPrefix(GatewayApiPaths.ROUTE_STRIP_PARTS)
                                .circuitBreaker(config -> config
                                        .setName("clearingService")
                                        .setFallbackUri("forward:/fallback/clearing")))
                        .uri(target(properties, CLEARING_SERVICE, services.getClearingUri())))
                .route("market-data-service", r -> r
                        .path(GatewayApiPaths.pattern("market"))
                        .filters(f -> f
                                .stripPrefix(GatewayApiPaths.ROUTE_STRIP_PARTS)
                                .circuitBreaker(config -> config
                                        .setName("marketDataService")
                                        .setFallbackUri("forward:/fallback/market")))
                        .uri(target(properties, MARKET_SERVICE, services.getMarketUri())))
                .build();
    }

    private static String target(GatewayProperties properties, String serviceId, String staticUri) {
        if (properties.getDiscovery().isConsulEnabled()) {
            return "lb://" + serviceId;
        }
        return staticUri;
    }
}
