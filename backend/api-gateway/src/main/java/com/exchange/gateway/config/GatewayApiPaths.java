package com.exchange.gateway.config;

public final class GatewayApiPaths {

    public static final String VERSION = "v1";
    public static final String PREFIX = "/api/" + VERSION;
    public static final int ROUTE_STRIP_PARTS = 3;

    public static final String IAM = PREFIX + "/iam";
    public static final String TRADING = PREFIX + "/trading";
    public static final String CLEARING = PREFIX + "/clearing";
    public static final String MARKET = PREFIX + "/market";

    private GatewayApiPaths() {
    }

    public static String pattern(String serviceSegment) {
        return PREFIX + "/" + serviceSegment + "/**";
    }
}
