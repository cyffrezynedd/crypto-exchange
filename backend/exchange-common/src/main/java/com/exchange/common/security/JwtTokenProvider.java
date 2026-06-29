package com.exchange.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtTokenProvider {

    public static final String CLAIM_USER_ID = "uid";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_TOKEN_TYPE = "type";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    public JwtTokenProvider(String secret, long accessTtlSeconds, long refreshTtlSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public TokenPair issueTokenPair(long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        String access = buildToken(userId, username, roles, TYPE_ACCESS, now.plusSeconds(accessTtlSeconds));
        String refresh = buildToken(userId, username, roles, TYPE_REFRESH, now.plusSeconds(refreshTtlSeconds));
        return new TokenPair(access, refresh, accessTtlSeconds);
    }

    public String issueAccessToken(long userId, String username, List<String> roles) {
        return buildToken(userId, username, roles, TYPE_ACCESS, Instant.now().plusSeconds(accessTtlSeconds));
    }

    public ParsedToken parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new ParsedToken(
                    claims.get(CLAIM_USER_ID, Long.class),
                    claims.get(CLAIM_USERNAME, String.class),
                    claims.get(CLAIM_ROLES, List.class),
                    claims.get(CLAIM_TOKEN_TYPE, String.class),
                    claims.getExpiration().toInstant()
            );
        } catch (ExpiredJwtException ex) {
            throw new JwtException("Token expired");
        } catch (JwtException ex) {
            throw new JwtException("Invalid token");
        }
    }

    public ParsedToken parseAccessToken(String token) {
        ParsedToken parsed = parse(token);
        if (!TYPE_ACCESS.equals(parsed.tokenType())) {
            throw new JwtException("Expected access token");
        }
        return parsed;
    }

    public ParsedToken parseRefreshToken(String token) {
        ParsedToken parsed = parse(token);
        if (!TYPE_REFRESH.equals(parsed.tokenType())) {
            throw new JwtException("Expected refresh token");
        }
        return parsed;
    }

    private String buildToken(long userId, String username, List<String> roles, String type, Instant expiresAt) {
        return Jwts.builder()
                .claims(Map.of(
                        CLAIM_USER_ID, userId,
                        CLAIM_USERNAME, username,
                        CLAIM_ROLES, roles,
                        CLAIM_TOKEN_TYPE, type
                ))
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public record TokenPair(String accessToken, String refreshToken, long expiresInSeconds) {
    }

    public record ParsedToken(
            Long userId,
            String username,
            List<String> roles,
            String tokenType,
            Instant expiresAt
    ) {
    }
}
