package com.nrn.gateway.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {

    private static final Logger logger = LogManager.getLogger(JwtUtils.class);
    public static final SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

    public static String getEmailFromJwtToken(String jwt) {
        logger.info("Extracting email from JWT token");
        try {
            jwt = jwt.substring(7); // Remove "Bearer " prefix
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

    public static String getUserIdFromJwtToken(String jwt) {
        logger.info("Extracting user ID from JWT token");
        try {
            jwt = jwt.substring(7); // Remove "Bearer " prefix
            SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String userId = String.valueOf(claims.get("userId"));
            logger.info("User ID extracted from token: {}", userId);
            return userId;

        } catch (Exception e) {
            logger.error("Failed to extract user ID from JWT: {}", e.getMessage());
            // Return null if user ID is not present (for backward compatibility)
            return null;
        }
    }

    public static String getAuthoritiesFromJwtToken(String jwt) {
        logger.info("Extracting authorities from JWT token");
        try {
            jwt = jwt.substring(7); // Remove "Bearer " prefix
            SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String authorities = String.valueOf(claims.get("authorities"));
            logger.info("Authorities extracted from token: {}", authorities);
            return authorities;

        } catch (Exception e) {
            logger.error("Failed to extract authorities from JWT: {}", e.getMessage());
            throw e;
        }
    }

    public static boolean validateJwtToken(String jwt) {
        logger.info("Validating JWT token");
        try {
            if (jwt == null || !jwt.startsWith("Bearer ")) {
                logger.warn("Invalid JWT token format");
                return false;
            }

            jwt = jwt.substring(7); // Remove "Bearer " prefix
            SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            // Check if token is expired
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                logger.warn("JWT token has expired");
                return false;
            }

            logger.info("JWT token is valid");
            return true;

        } catch (Exception e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }
}