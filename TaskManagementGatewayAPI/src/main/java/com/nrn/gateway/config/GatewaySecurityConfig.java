package com.nrn.gateway.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.nrn.gateway.filter.JwtAuthenticationWebFilter;
import com.nrn.gateway.filter.CorsWebFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration for API Gateway
 * Integrates with existing JWT authentication filter while providing enhanced security features
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    private static final Logger logger = LogManager.getLogger(GatewaySecurityConfig.class);

    @Autowired
    private JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Autowired
    private CorsWebFilter corsWebFilter;

    @Value("${spring.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${spring.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${spring.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${spring.cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        logger.info("Configuring Spring Security for Gateway with JWT integration");

        return http
            // Add our custom CORS filter before any other filters
            .addFilterAt(corsWebFilter, SecurityWebFiltersOrder.CORS)
            
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Path-based security configuration - enhanced declarative security
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**", "/actuator/**").permitAll()  // Public authentication endpoints
                .pathMatchers("/").permitAll()                         // Root path
                .pathMatchers("/api/**").authenticated()               // API endpoints require authentication
                .anyExchange().authenticated()                         // All other endpoints require authentication
            )
            
            // Disable unnecessary features for API Gateway
            .csrf(csrf -> csrf.disable())                    // CSRF not needed for stateless API
            .formLogin(form -> form.disable())               // No form-based login in API Gateway
            .httpBasic(basic -> basic.disable())             // No basic auth in API Gateway
            .logout(logout -> logout.disable())              // No logout handling in API Gateway
            
            // Integrate existing JWT filter into Spring Security filter chain
            .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            
            // Enhanced error handling
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler())
            )
            
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        configuration.setAllowCredentials(allowCredentials);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ServerAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public ServerAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}