package com.exchange.gateway.filter;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.security.JwtTokenProvider;
import com.exchange.gateway.config.GatewayProperties;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final GatewayProperties properties;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthGlobalFilter(GatewayProperties properties, JwtTokenProvider jwtTokenProvider) {
        this.properties = properties;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.getJwt().isEnabled()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        if (isPublicPath(request)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange, ErrorCode.UNAUTHENTICATED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        try {
            JwtTokenProvider.ParsedToken parsed = jwtTokenProvider.parseAccessToken(token);
            String roles = parsed.roles() == null ? "" : parsed.roles().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));

            ServerHttpRequest mutated = request.mutate()
                    .header("X-User-Id", String.valueOf(parsed.userId()))
                    .header("X-Username", parsed.username())
                    .header("X-User-Roles", roles)
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (JwtException ex) {
            return unauthorized(exchange, ErrorCode.TOKEN_INVALID, ex.getMessage());
        }
    }

    private boolean isPublicPath(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();
        List<String> publicPaths = properties.getPublicPaths();

        for (String pattern : publicPaths) {
            if (pattern.contains(":")) {
                String[] parts = pattern.split(":", 2);
                HttpMethod allowedMethod = HttpMethod.valueOf(parts[0]);
                if (method == allowedMethod && PATH_MATCHER.match(parts[1], path)) {
                    return true;
                }
            } else if (PATH_MATCHER.match(pattern + "/**", path) || PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, ErrorCode code, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(
                "{\"error\":\"Unauthorized\",\"code\":\"%s\",\"message\":\"%s\"}",
                code.name(),
                message.replace("\"", "'"));
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
