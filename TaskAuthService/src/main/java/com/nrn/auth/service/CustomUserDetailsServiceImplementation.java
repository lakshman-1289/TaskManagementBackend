package com.nrn.auth.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nrn.auth.model.User;
import com.nrn.auth.repository.UserRepository;

@Service
public class CustomUserDetailsServiceImplementation implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(CustomUserDetailsServiceImplementation.class);

    @Autowired
    private UserRepository userRepository;

    public CustomUserDetailsServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", username);

        User user = userRepository.findByEmail(username);

        if (user == null) {
            logger.error("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        logger.info("User found: {} with role: {}", username, user.getRole());

        // Convert user role to GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
            // Ensure role has ROLE_ prefix for Spring Security
            String role = user.getRole().trim();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            authorities.add(new SimpleGrantedAuthority(role));
            logger.info("Assigned authority: {} to user: {}", role, username);
        } else {
            // Default role if none specified
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            logger.info("Assigned default ROLE_USER to user: {}", username);
        }

        logger.info("Final authorities for user {}: {}", username, authorities);

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}