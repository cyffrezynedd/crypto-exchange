package com.exchange.common.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;

public final class GatewayUserContext {

    private GatewayUserContext() {
    }

    public static void requireAdmin(String roles) {
        if (!hasRole(roles, "ADMIN")) {
            throw new ExchangeException(ErrorCode.PERMISSION_DENIED, "Admin role required");
        }
    }

    public static void requireSelfOrAdmin(Long currentUserId, String roles, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            return;
        }
        requireAdmin(roles);
    }

    private static boolean hasRole(String roles, String role) {
        if (roles == null || roles.isBlank()) {
            return false;
        }
        for (String value : roles.split(",")) {
            if (role.equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }
}
