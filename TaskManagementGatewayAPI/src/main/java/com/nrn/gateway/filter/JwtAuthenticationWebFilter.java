package com.nrn.gateway.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.nrn.gateway.config.JwtConstants;
import com.nrn.gateway.config.JwtUtils;

import reactor.core.publisher.Mono;

/**
 * JWT Authentication WebFilter for Spring Security integration
 * Maintains exact same functionality as original JwtAuthenticationFilter
 * but integrates with Spring Security reactive framework
 */
@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        logger.info("Processing request: {} {}", request.getMethod(), path);

        // Skip JWT processing for public paths - Spring Security will handle authorization
        if (path.startsWith("/auth/") || path.equals("/")) {
            logger.info("Skipping JWT validation for path: {}", path);
            return chain.filter(exchange);
        }

        // For OPTIONS requests, let Spring Security handle CORS
        if (request.getMethod().name().equals("OPTIONS")) {
            logger.info("Skipping JWT validation for OPTIONS request on path: {}", path);
            return chain.filter(exchange);
        }

        // Check for JWT token in header
        String jwt = request.getHeaders().getFirst(JwtConstants.JWT_HEADER);
        
        if (jwt == null || jwt.isEmpty()) {
            logger.warn("No JWT token found in request header for path: {}", path);
            // Let Spring Security handle missing authentication
            return chain.filter(exchange);
        }

        // Validate JWT token
        if (!JwtUtils.validateJwtToken(jwt)) {
            logger.warn("Invalid JWT token for path: {}", path);
            // Let Spring Security handle invalid authentication
            return chain.filter(exchange);
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
                // Let Spring Security handle invalid authentication
                return chain.filter(exchange);
            }

            // Create custom authentication token for Spring Security
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(email, userId, authorities);
            SecurityContext context = new SecurityContextImpl(authentication);

            // Add minimal user context to headers for downstream services
            // Following microservices best practice: only pass essential data
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Authorities", authorities)
                    .build();

            logger.info("Added essential user context headers for downstream services (userId: {})", userId);

            // Set authentication in reactive security context and continue with modified request
            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
            // Let Spring Security handle authentication error
            return chain.filter(exchange);
        }
    }
}