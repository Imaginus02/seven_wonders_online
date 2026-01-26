package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.UserDAO;
import com.reynaud.wonders.dto.UserDTO;
import com.reynaud.wonders.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("SecurePass123");
        userDTO.setConfirmPassword("SecurePass123");

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setPassword("encodedPassword");
    }

    @Test
    void testRegisterUserSuccess() {
        when(userDAO.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedPassword");
        when(userDAO.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity result = userService.registerUser(userDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        verify(userDAO, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("SecurePass123");
        verify(userDAO, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(userDAO.findByUsername("testuser")).thenReturn(userEntity);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(userDTO),
                "Username already exists");

        verify(userDAO, times(1)).findByUsername("testuser");
        verify(userDAO, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUserWithUppercaseUsername() {
        UserDTO uppercaseDTO = new UserDTO();
        uppercaseDTO.setUsername("TestUser");
        uppercaseDTO.setPassword("SecurePass123");
        uppercaseDTO.setConfirmPassword("SecurePass123");

        when(userDAO.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedPassword");
        when(userDAO.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity result = userService.registerUser(uppercaseDTO);

        assertNotNull(result);
        verify(userDAO).findByUsername("testuser");
    }

    @Test
    void testRegisterUserWithWhitespace() {
        UserDTO whitespaceDTO = new UserDTO();
        whitespaceDTO.setUsername("  testuser  ");
        whitespaceDTO.setPassword("SecurePass123");
        whitespaceDTO.setConfirmPassword("SecurePass123");

        when(userDAO.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedPassword");
        when(userDAO.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity result = userService.registerUser(whitespaceDTO);

        assertNotNull(result);
        verify(userDAO).findByUsername("testuser");
    }

    @Test
    void testUsernameExistsTrue() {
        when(userDAO.findByUsername("testuser")).thenReturn(userEntity);

        boolean exists = userService.usernameExists("testuser");

        assertTrue(exists);
        verify(userDAO, times(1)).findByUsername("testuser");
    }

    @Test
    void testUsernameExistsFalse() {
        when(userDAO.findByUsername("nonexistent")).thenReturn(null);

        boolean exists = userService.usernameExists("nonexistent");

        assertFalse(exists);
        verify(userDAO, times(1)).findByUsername("nonexistent");
    }
}
