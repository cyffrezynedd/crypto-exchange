package com.exchange.common.web;

public final class GatewayHeaders {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";
    public static final String ROLES_HEADER = "X-User-Roles";
    public static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";
    public static final String INTERNAL_API_HEADER = "X-Internal-Api-Key";

    private GatewayHeaders() {
    }
}
