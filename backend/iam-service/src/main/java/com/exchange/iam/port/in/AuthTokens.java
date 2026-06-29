package com.exchange.iam.port.in;

import com.exchange.common.security.JwtTokenProvider;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        Long userId,
        String username
) {
    public static AuthTokens from(JwtTokenProvider.TokenPair pair, Long userId, String username) {
        return new AuthTokens(pair.accessToken(), pair.refreshToken(), pair.expiresInSeconds(), userId, username);
    }
}
