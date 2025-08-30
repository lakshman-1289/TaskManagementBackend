package com.nrn.gateway.config;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Custom access denied handler for role-based authorization failures
 * Provides consistent error responses for authorization issues
 */
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    private static final Logger logger = LogManager.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        String path = exchange.getRequest().getPath().value();
        logger.warn("Access denied for path: {} - Reason: {}", path, denied.getMessage());
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        // Maintain consistent error format
        String body = String.format(
            "{\"error\":\"%s\", \"status\":%d, \"path\":\"%s\", \"timestamp\":\"%s\"}", 
            "Forbidden - Insufficient privileges", 
            HttpStatus.FORBIDDEN.value(),
            path,
            Instant.now().toString()
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}