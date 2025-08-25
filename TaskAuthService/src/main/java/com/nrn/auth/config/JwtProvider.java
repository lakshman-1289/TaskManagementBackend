package com.nrn.auth.config;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.nrn.auth.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {

    private static final Logger logger = LogManager.getLogger(JwtProvider.class);
    public static final SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());


    /**
     * Generate JWT token with userId for centralized Gateway validation
     * This is the primary method used for authentication
     */
    public static String generateToken(Authentication auth, User user) {
        logger.info("Generating JWT token with user ID for user: {}", auth.getName());

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        logger.debug("User roles extracted for token: {}", roles);

        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .claim("email", auth.getName())
                .claim("userId", user.getId())
                .claim("authorities", roles)
                .signWith(key)
                .compact();

        logger.info("JWT token generated successfully for user: {} with ID: {}", auth.getName(), user.getId());
        return jwt;
    }

    /**
     * Helper method to convert authorities to comma-separated string
     */
    public static String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        logger.info("Populating authorities from GrantedAuthority collection");

        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            logger.trace("Authority found: {}", authority.getAuthority());
            auths.add(authority.getAuthority());
        }

        String authoritiesString = String.join(",", auths);
        logger.info("Final comma-separated authorities: {}", authoritiesString);
        return authoritiesString;
    }
}