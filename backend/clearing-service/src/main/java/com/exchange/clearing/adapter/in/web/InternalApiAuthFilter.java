package com.exchange.clearing.adapter.in.web;

import com.exchange.clearing.config.InternalApiProperties;
import com.exchange.common.error.ErrorCode;
import com.exchange.common.web.GatewayHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 15)
public class InternalApiAuthFilter extends OncePerRequestFilter {

    private final InternalApiProperties properties;

    public InternalApiAuthFilter(InternalApiProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String expected = properties.getKey();
        if (expected == null || expected.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        String provided = request.getHeader(GatewayHeaders.INTERNAL_API_HEADER);
        if (!expected.equals(provided)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(
                    "{\"error\":\"Unauthorized\",\"code\":\"%s\",\"message\":\"Invalid internal API key\"}"
                            .formatted(ErrorCode.UNAUTHENTICATED.name()));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
