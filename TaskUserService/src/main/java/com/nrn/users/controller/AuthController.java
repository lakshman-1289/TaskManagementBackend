package com.nrn.users.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nrn.users.config.JwtProvider;
import com.nrn.users.model.AuthResponse;
import com.nrn.users.model.LoginRequest;
import com.nrn.users.model.User;
import com.nrn.users.repository.UserRepository;
import com.nrn.users.service.CustomUserDetailsServiceImplementation;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsServiceImplementation customUserDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        logger.info("Signup request received for email: {}", user.getEmail());

        String email = user.getEmail();
        String password = user.getPassword();
        String fullName = user.getFullName();
        String role = user.getRole();

        User isEmailExist = userRepository.findByEmail(email);
        if (isEmailExist != null) {
            logger.warn("Signup failed - email already in use: {}", email);
            throw new Exception("Email is already used with another account!");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(createdUser);
        logger.info("New user registered successfully: {}", savedUser.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);
        logger.info("JWT generated for registered user: {}", email);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("Register Success");
        res.setStatus(true);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        logger.info("Login request received for user: {}", username);

        Authentication authentication = authenticate(username, loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);
        logger.info("JWT generated for logged-in user: {}", username);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("Login Success");
        res.setStatus(true);

        logger.info("User logged in successfully: {}", username);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        logger.info("Authenticating user: {}", username);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            logger.error("Authentication failed - user not found: {}", username);
            throw new BadCredentialsException("Invalid Username or password!");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            logger.error("Authentication failed - password mismatch for user: {}", username);
            throw new BadCredentialsException("Invalid Username or password!");
        }

        logger.info("User authenticated successfully: {}", username);
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }
}
