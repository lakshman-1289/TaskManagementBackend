package com.nrn.users.config;

import java.util.Arrays;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class ApplicationConfiguration {

    private static final Logger logger = LogManager.getLogger(ApplicationConfiguration.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Initializing SecurityFilterChain...");

        http.sessionManagement(management -> {
            logger.info("Setting session management policy to STATELESS");
            management.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }).authorizeHttpRequests(authorize -> {
            logger.info("Configuring authorization rules: /api/** requires authentication, all others are permitted");
            authorize.requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll();
        }).addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class);

        logger.info("JWT token validator filter added before BasicAuthenticationFilter");

        http.csrf(csrf -> {
            logger.info("Disabling CSRF protection (stateless API)");
            csrf.disable();
        });

        http.cors(cors -> {
            logger.info("Enabling CORS with custom configuration");
            cors.configurationSource(corsConfigurationSource());
        });

        http.httpBasic(Customizer.withDefaults());
        http.formLogin(Customizer.withDefaults());

        logger.info("SecurityFilterChain built successfully");
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        logger.info("Creating CORS configuration source");

        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                logger.info("Applying CORS configuration for request: {}", request.getRequestURI());

                CorsConfiguration cfg = new CorsConfiguration();
                cfg.setAllowedOrigins(Collections.singletonList("*"));
                cfg.setAllowedMethods(Collections.singletonList("*"));
                cfg.setAllowedHeaders(Collections.singletonList("*"));
                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                cfg.setAllowCredentials(true);
                cfg.setMaxAge(3600L);

                logger.info("CORS configuration applied");
                return cfg;
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        logger.info("Creating PasswordEncoder bean using BCrypt");
        return new BCryptPasswordEncoder();
    }
}
