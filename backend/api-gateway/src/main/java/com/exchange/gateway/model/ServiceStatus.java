package com.exchange.gateway.model;

public record ServiceStatus(
        String name,
        String routePrefix,
        String status,
        String detail
) {
}
