package com.exchange.common.web;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class GatewayTrustFilter extends OncePerRequestFilter {

    private final GatewayTrustProperties properties;

    public GatewayTrustFilter(GatewayTrustProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String expected = properties.getSecret();
        if (expected == null || expected.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        String provided = request.getHeader(GatewayHeaders.GATEWAY_SECRET_HEADER);
        if (!expected.equals(provided)) {
            writeUnauthorized(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"error\":\"Unauthorized\",\"code\":\"%s\",\"message\":\"Direct service access is not allowed\"}"
                        .formatted(ErrorCode.UNAUTHENTICATED.name()));
    }
}
