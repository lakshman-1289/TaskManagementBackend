package com.nrn.gateway.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Custom Authentication token for JWT-based authentication
 * Integrates with Spring Security while maintaining user context information
 */
public class JwtAuthenticationToken implements Authentication {

    private final String email;
    private final String userId;
    private final String authorities;
    private final Collection<GrantedAuthority> grantedAuthorities;
    private boolean authenticated = true;

    public JwtAuthenticationToken(String email, String userId, String authorities) {
        this.email = email;
        this.userId = userId;
        this.authorities = authorities;
        this.grantedAuthorities = parseAuthorities(authorities);
    }

    private Collection<GrantedAuthority> parseAuthorities(String authorities) {
        if (authorities == null || authorities.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(authorities.split(","))
                .map(String::trim)
                .filter(auth -> !auth.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return null; // JWT tokens are stateless, no credentials stored
    }

    @Override
    public Object getDetails() {
        return userId; // Store userId in details for easy access
    }

    @Override
    public Object getPrincipal() {
        return email; // Email as principal identifier
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return email;
    }

    // Utility methods for easy access to user context
    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthoritiesString() {
        return authorities;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationToken{" +
                "email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", authorities='" + authorities + '\'' +
                ", authenticated=" + authenticated +
                '}';
    }
}