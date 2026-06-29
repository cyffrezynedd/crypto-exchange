package com.exchange.common.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {

    private static final String SECRET = "dev-only-change-me-exchange-jwt-secret-key-32chars";

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, 900, 604800);
    }

    @Test
    void issuesAccessAndRefreshPair() {
        JwtTokenProvider.TokenPair pair = provider.issueTokenPair(99L, "neo", List.of("USER"));

        assertNotNull(pair.accessToken());
        assertNotNull(pair.refreshToken());
        assertEquals(900, pair.expiresInSeconds());
    }

    @Test
    void parsesAccessTokenClaims() {
        String access = provider.issueAccessToken(7L, "trinity", List.of("USER", "ADMIN"));

        JwtTokenProvider.ParsedToken parsed = provider.parseAccessToken(access);

        assertEquals(7L, parsed.userId());
        assertEquals("trinity", parsed.username());
        assertEquals(List.of("USER", "ADMIN"), parsed.roles());
        assertEquals(JwtTokenProvider.TYPE_ACCESS, parsed.tokenType());
    }

    @Test
    void rejectsRefreshTokenWhenAccessExpected() {
        String refresh = provider.issueTokenPair(1L, "morpheus", List.of("USER")).refreshToken();

        assertThrows(JwtException.class, () -> provider.parseAccessToken(refresh));
    }

    @Test
    void rejectsAccessTokenWhenRefreshExpected() {
        String access = provider.issueAccessToken(1L, "morpheus", List.of("USER"));

        assertThrows(JwtException.class, () -> provider.parseRefreshToken(access));
    }
}
