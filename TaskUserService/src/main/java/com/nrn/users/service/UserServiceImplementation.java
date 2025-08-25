package com.nrn.users.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrn.users.model.User;
import com.nrn.users.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImplementation.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            logger.info("User found with ID: {} (email: {})", userId, user.getEmail());
        } else {
            logger.warn("No user found with ID: {}", userId);
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            logger.info("User found with email: {}", email);
        } else {
            logger.warn("No user found with email: {}", email);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Fetching all users from database");
        List<User> users = userRepository.findAll();
        logger.info("Total users fetched: {}", users.size());
        return users;
    }
}
