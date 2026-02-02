package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.UserDAO;
import com.reynaud.wonders.dto.UserDTO;
import com.reynaud.wonders.entity.UserEntity;
import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final LoggingService loggingService;

    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder, LoggingService loggingService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.loggingService = loggingService;
    }

    @Transactional
    public UserEntity registerUser(UserDTO userDTO) {
        String normalizedUsername = userDTO.getUsername().trim().toLowerCase(Locale.ROOT);
        loggingService.info("Registering new user - Username: " + normalizedUsername, "UserService.registerUser");

        UserEntity existingUser = userDAO.findByUsername(normalizedUsername);
        if (existingUser != null) {
            loggingService.warning("Registration failed - Username already exists - Username: " + normalizedUsername, "UserService.registerUser");
            throw new IllegalArgumentException("Username already exists");
        }

        // Create new user entity
        UserEntity newUser = new UserEntity();
        newUser.setUsername(normalizedUsername);
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Save and return the user
        UserEntity savedUser = userDAO.save(newUser);
        loggingService.info("User registered successfully - Username: " + normalizedUsername + ", UserID: " + savedUser.getId(), "UserService.registerUser");
        return savedUser;
    }

    public boolean usernameExists(String username) {
        return userDAO.findByUsername(username) != null;
    }

    @Transactional(readOnly = true)
    public UserEntity findByUsername(String username) {
        //loggingService.debug("Finding user by username - Username: " + username, "UserService.findByUsername");
        UserEntity user = userDAO.findByUsername(username);
        //if (user != null) {
        //    loggingService.debug("User found - Username: " + username + ", UserID: " + user.getId(), "UserService.findByUsername");
        //} else {
        //    loggingService.debug("User not found - Username: " + username, "UserService.findByUsername");
        //}
        return user;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        return userDAO.findAll();
    }

    @Transactional(readOnly = true)
    public UserEntity findByIdIfExists(Long id) {
        loggingService.debug("Finding user by ID - UserID: " + id, "UserService.findByIdIfExists");
        UserEntity user = userDAO.findById(id).orElse(null);
        if (user != null) {
            loggingService.debug("User found - UserID: " + id + ", Username: " + user.getUsername(), "UserService.findByIdIfExists");
        } else {
            loggingService.debug("User not found - UserID: " + id, "UserService.findByIdIfExists");
        }
        return user;
    }
}
