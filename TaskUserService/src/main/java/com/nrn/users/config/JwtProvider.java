package com.nrn.users.config;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {

    private static final Logger logger = LogManager.getLogger(JwtProvider.class);
    public static final SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

    public static String generateToken(Authentication auth) {
        logger.info("Generating JWT token for user: {}", auth.getName());

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        logger.debug("User roles extracted for token: {}", roles);

        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();

        logger.info("JWT token generated successfully for user: {}", auth.getName());
        return jwt;
    }

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

    public static String getEmailFromJwtToken(String jwt) {
        logger.info("Extracting email from JWT token");
        try {
            jwt = jwt.substring(7);
            SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String email = String.valueOf(claims.get("email"));
            logger.info("Email extracted from token: {}", email);
            return email;

        } catch (Exception e) {
            logger.error("Failed to extract email from JWT: {}", e.getMessage());
            throw e;
        }
    }
}
