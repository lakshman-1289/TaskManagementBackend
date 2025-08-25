package com.nrn.auth.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationConfiguration {

    private static final Logger logger = LogManager.getLogger(ApplicationConfiguration.class);

    /**
     * Simple SecurityFilterChain for AuthService
     * - All endpoints are public (authentication endpoints should be accessible)
     * - No JWT validation needed (Gateway handles this for protected services)
     * - Only provides password encoding for user registration/authentication
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring minimal SecurityFilterChain for AuthService - all endpoints public");

        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for REST API
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());  // All endpoints public

        logger.info("SecurityFilterChain configured: CSRF disabled, stateless sessions, all requests permitted");
        return http.build();
    }

    /**
     * Password encoder for user registration and authentication
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        logger.info("Creating BCryptPasswordEncoder for password hashing");
        return new BCryptPasswordEncoder();
    }
}