package com.reynaud.wonders.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDTOTest {

    @Autowired
    private Validator validator;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setPassword("SecurePass123");
        userDTO.setConfirmPassword("SecurePass123");
    }

    @Test
    void testIdGetterSetter() {
        userDTO.setId(2L);
        assertEquals(2L, userDTO.getId());
    }

    @Test
    void testUsernameGetterSetter() {
        userDTO.setUsername("newuser");
        assertEquals("newuser", userDTO.getUsername());
    }

    @Test
    void testPasswordGetterSetter() {
        userDTO.setPassword("NewSecurePass123");
        assertEquals("NewSecurePass123", userDTO.getPassword());
    }

    @Test
    void testConfirmPasswordGetterSetter() {
        userDTO.setConfirmPassword("NewSecurePass123");
        assertEquals("NewSecurePass123", userDTO.getConfirmPassword());
    }

    @Test
    void testValidUserDTO() {
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankUsername() {
        userDTO.setUsername("");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void testNullUsername() {
        userDTO.setUsername(null);
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testUsernameTooShort() {
        userDTO.setUsername("ab");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testUsernameTooLong() {
        userDTO.setUsername("a".repeat(51));
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankPassword() {
        userDTO.setPassword("");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullPassword() {
        userDTO.setPassword(null);
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testPasswordTooShort() {
        userDTO.setPassword("Pass1");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testPasswordTooLong() {
        userDTO.setPassword("P1".repeat(37));
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testPasswordWithoutNumber() {
        userDTO.setPassword("SecurePassword");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testPasswordWithoutLetter() {
        userDTO.setPassword("12345678");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankConfirmPassword() {
        userDTO.setConfirmPassword("");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullConfirmPassword() {
        userDTO.setConfirmPassword(null);
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidPasswordPatterns() {
        String[] validPasswords = {
                "Password1",
                "SecurePass123",
                "Test@1234",
                "MyP@ssw0rd",
                "a1bcdefgh"
        };

        for (String password : validPasswords) {
            userDTO.setPassword(password);
            userDTO.setConfirmPassword(password);
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
            assertTrue(violations.isEmpty(), "Password " + password + " should be valid");
        }
    }

    @Test
    void testMinimumValidUsername() {
        userDTO.setUsername("abc");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testMaximumValidUsername() {
        userDTO.setUsername("a".repeat(50));
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testMinimumValidPassword() {
        userDTO.setPassword("Passw012");
        userDTO.setConfirmPassword("Passw012");
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty(), "Should be valid with 8+ characters including letter and number");
    }

    @Test
    void testMaximumValidPassword() {
        String maxPassword = "P".repeat(36) + "a1";
        userDTO.setPassword(maxPassword);
        userDTO.setConfirmPassword(maxPassword);
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        assertTrue(violations.isEmpty());
    }
}
