package com.nrn.gateway.config;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Custom authentication entry point for handling authentication failures
 * Provides consistent error responses matching existing filter behavior
 */
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private static final Logger logger = LogManager.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String path = exchange.getRequest().getPath().value();
        logger.error("Authentication failed for path: {} - Reason: {}", path, ex.getMessage());
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        // Maintain same error format as existing JwtAuthenticationFilter
        String body = String.format(
            "{\"error\":\"%s\", \"status\":%d, \"path\":\"%s\", \"timestamp\":\"%s\"}", 
            "Unauthorized - Authentication required", 
            HttpStatus.UNAUTHORIZED.value(),
            path,
            Instant.now().toString()
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}