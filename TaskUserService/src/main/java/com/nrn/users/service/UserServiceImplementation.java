package com.nrn.users.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrn.users.config.JwtProvider;
import com.nrn.users.model.User;
import com.nrn.users.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImplementation.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserProfileByJwt(String jwt) {
        logger.info("Extracting user profile from JWT");
        String email = JwtProvider.getEmailFromJwtToken(jwt);
        logger.info("Extracted email from JWT: {}", email);

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
