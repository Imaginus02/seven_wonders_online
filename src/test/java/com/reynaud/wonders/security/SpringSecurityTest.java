package com.reynaud.wonders.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringSecurityTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder);
    }

    @Test
    void testPasswordEncode() {
        String rawPassword = "TestPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertNotNull(encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testBCryptPasswordEncoding() {
        String password = "TestPassword123";
        String encoded1 = passwordEncoder.encode(password);
        String encoded2 = passwordEncoder.encode(password);

        // Same password should produce different encodings (bcrypt uses salt)
        assertNotEquals(encoded1, encoded2);

        // But both should match the original password
        assertTrue(passwordEncoder.matches(password, encoded1));
        assertTrue(passwordEncoder.matches(password, encoded2));
    }

    @Test
    void testPasswordMismatch() {
        String password = "TestPassword123";
        String encodedPassword = passwordEncoder.encode(password);
        
        assertFalse(passwordEncoder.matches("WrongPassword", encodedPassword));
    }
}
