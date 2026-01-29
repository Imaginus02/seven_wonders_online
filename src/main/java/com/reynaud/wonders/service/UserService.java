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

    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserEntity registerUser(UserDTO userDTO) {
        String normalizedUsername = userDTO.getUsername().trim().toLowerCase(Locale.ROOT);

        UserEntity existingUser = userDAO.findByUsername(normalizedUsername);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Create new user entity
        UserEntity newUser = new UserEntity();
        newUser.setUsername(normalizedUsername);
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Save and return the user
        return userDAO.save(newUser);
    }

    public boolean usernameExists(String username) {
        return userDAO.findByUsername(username) != null;
    }

    @Transactional(readOnly = true)
    public UserEntity findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        return userDAO.findAll();
    }

    @Transactional(readOnly = true)
    public UserEntity findByIdIfExists(Long id) {
        return userDAO.findById(id).orElse(null);
    }
}
