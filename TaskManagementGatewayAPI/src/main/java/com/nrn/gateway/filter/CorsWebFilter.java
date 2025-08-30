package com.nrn.gateway.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorsWebFilter implements WebFilter {

    private static final Logger logger = LogManager.getLogger(CorsWebFilter.class);

    @Value("${spring.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${spring.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${spring.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${spring.cors.allow-credentials}")
    private boolean allowCredentials;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();
        
        // Handle CORS preflight (OPTIONS) requests
        if (request.getMethod().name().equals("OPTIONS")) {
            logger.info("Handling CORS preflight request for path: {}", request.getPath());
            
            String origin = request.getHeaders().getOrigin();
            String[] allowedOriginArray = allowedOrigins.split(",");
            
            // Check if the origin is in our allowed list
            boolean isOriginAllowed = false;
            String allowedOrigin = null;
            
            if (origin != null) {
                for (String allowed : allowedOriginArray) {
                    allowed = allowed.trim();
                    if (allowed.equals(origin) || allowed.equals("*")) {
                        isOriginAllowed = true;
                        allowedOrigin = allowed.equals("*") ? origin : allowed;
                        break;
                    }
                }
            }
            
            // If no specific origin is allowed or origin is null, use the first allowed origin
            if (allowedOrigin == null && allowedOriginArray.length > 0) {
                allowedOrigin = allowedOriginArray[0].trim();
            }
            
            response.getHeaders().add("Access-Control-Allow-Origin", allowedOrigin);
            response.getHeaders().add("Access-Control-Allow-Methods", allowedMethods);
            response.getHeaders().add("Access-Control-Allow-Headers", allowedHeaders);
            response.getHeaders().add("Access-Control-Allow-Credentials", String.valueOf(allowCredentials));
            response.getHeaders().add("Access-Control-Max-Age", "3600");
            
            response.setStatusCode(org.springframework.http.HttpStatus.OK);
            return Mono.empty();
        }
        
        // For non-OPTIONS requests, continue with the filter chain
        return chain.filter(exchange);
    }
}