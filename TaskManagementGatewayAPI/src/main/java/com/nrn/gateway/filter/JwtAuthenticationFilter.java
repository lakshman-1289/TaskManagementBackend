package com.nrn.gateway.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.nrn.gateway.config.JwtConstants;
import com.nrn.gateway.config.JwtUtils;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            logger.info("Processing request: {} {}", request.getMethod(), request.getPath());

            // Skip authentication for auth endpoints and root path
            String path = request.getPath().value();
            if (path.startsWith("/auth/") || path.equals("/")) {
                logger.info("Skipping JWT validation for path: {}", path);
                return chain.filter(exchange);
            }

            // Check for JWT token in header
            String jwt = request.getHeaders().getFirst(JwtConstants.JWT_HEADER);
            
            if (jwt == null || jwt.isEmpty()) {
                logger.warn("No JWT token found in request header for path: {}", path);
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            // Validate JWT token
            if (!JwtUtils.validateJwtToken(jwt)) {
                logger.warn("Invalid JWT token for path: {}", path);
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Extract essential user information from JWT
                String email = JwtUtils.getEmailFromJwtToken(jwt);
                String authorities = JwtUtils.getAuthoritiesFromJwtToken(jwt);
                String userId = JwtUtils.getUserIdFromJwtToken(jwt);

                logger.info("JWT validation successful for user: {} (ID: {}) with authorities: {}", email, userId, authorities);

                // Validate that userId is present in JWT
                if (userId == null || "null".equals(userId)) {
                    logger.error("JWT token missing userId claim for user: {}", email);
                    return onError(exchange, "Invalid JWT token: missing user ID", HttpStatus.UNAUTHORIZED);
                }

                // Add minimal user context to headers for downstream services
                // Following microservices best practice: only pass essential data
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Authorities", authorities)
                        .build();

                logger.info("Added essential user context headers for downstream services (userId: {})", userId);

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                logger.error("Error processing JWT token: {}", e.getMessage());
                return onError(exchange, "Error processing JWT token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        logger.error("Authentication error: {} for path: {}", message, exchange.getRequest().getPath());
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format(
            "{\"error\":\"%s\", \"status\":%d, \"path\":\"%s\", \"timestamp\":\"%s\"}", 
            message, 
            httpStatus.value(),
            exchange.getRequest().getPath().value(),
            java.time.Instant.now().toString()
        );
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}