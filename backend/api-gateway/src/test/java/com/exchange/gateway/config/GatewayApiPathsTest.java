package com.exchange.gateway.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GatewayApiPathsTest {

    @Test
    void exposesVersionedServicePrefixes() {
        assertEquals("v1", GatewayApiPaths.VERSION);
        assertEquals("/api/v1/iam", GatewayApiPaths.IAM);
        assertEquals("/api/v1/trading", GatewayApiPaths.TRADING);
        assertEquals("/api/v1/clearing", GatewayApiPaths.CLEARING);
        assertEquals("/api/v1/market", GatewayApiPaths.MARKET);
    }

    @Test
    void buildsRoutePatterns() {
        assertEquals("/api/v1/iam/**", GatewayApiPaths.pattern("iam"));
    }

    @Test
    void stripsApiVersionAndServiceSegment() {
        assertEquals(3, GatewayApiPaths.ROUTE_STRIP_PARTS);
    }
}
