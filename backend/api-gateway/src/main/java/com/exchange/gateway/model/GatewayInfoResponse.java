package com.exchange.gateway.model;

import java.util.List;

public record GatewayInfoResponse(
        String name,
        String version,
        String apiVersion,
        List<ServiceStatus> services
) {
}
