package com.exchange.iam.adapter.in.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GatewayUserContextTest {

    @Test
    void allowsSelfAccess() {
        assertDoesNotThrow(() -> GatewayUserContext.requireSelfOrAdmin(5L, "USER", 5L));
    }

    @Test
    void allowsAdminAccessToOtherUser() {
        assertDoesNotThrow(() -> GatewayUserContext.requireSelfOrAdmin(1L, "USER,ADMIN", 9L));
    }

    @Test
    void deniesForeignUser() {
        ExchangeException ex = assertThrows(ExchangeException.class,
                () -> GatewayUserContext.requireSelfOrAdmin(1L, "USER", 9L));
        assertEquals(ErrorCode.PERMISSION_DENIED, ex.code());
    }

    @Test
    void requiresAdminForList() {
        ExchangeException ex = assertThrows(ExchangeException.class,
                () -> GatewayUserContext.requireAdmin("USER"));
        assertEquals(ErrorCode.PERMISSION_DENIED, ex.code());
    }
}
