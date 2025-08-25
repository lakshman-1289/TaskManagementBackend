package com.nrn.users.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nrn.users.model.User;
import com.nrn.users.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(
            @RequestHeader("X-User-Id") String userId) {
        logger.info("Received request to fetch user profile for userId: {}", userId);

        try {
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("No userId provided in header");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Long userIdLong;
            try {
                userIdLong = Long.valueOf(userId);
                if (userIdLong <= 0) {
                    logger.error("Invalid userId value: {}", userId);
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid userId format: {}", userId, e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            User user = userService.getUserById(userIdLong);
            if (user == null) {
                logger.error("No user found with userId: {}", userId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            logger.info("User profile fetched successfully: {}", user.getEmail());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch user profile for userId {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Authorities") String userAuthorities) {
        logger.info("Received request to fetch all users from userId: {} with authorities: {}", userId, userAuthorities);

        try {
            // Validate userId header
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("No userId provided in header for getAllUsers request");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Validate authorities header
            if (userAuthorities == null || userAuthorities.trim().isEmpty() || "null".equals(userAuthorities)) {
                logger.error("No user authorities provided in header for getAllUsers request. Authorities received: '{}'", userAuthorities);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            // Check for admin role
            if (!userAuthorities.contains("ROLE_ADMIN")) {
                logger.warn("Access denied: User {} with authorities {} attempted to access getAllUsers endpoint", userId, userAuthorities);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            logger.info("Admin access granted for userId: {} to fetch all users", userId);
            List<User> users = userService.getAllUsers();
            logger.info("Total users fetched by admin {}: {}", userId, users.size());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch users for admin userId {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
