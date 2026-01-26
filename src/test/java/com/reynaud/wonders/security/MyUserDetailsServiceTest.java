package com.reynaud.wonders.security;

import com.reynaud.wonders.dao.UserDAO;
import com.reynaud.wonders.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setPassword("encodedPassword");
        userEntity.setRole("ROLE_USER");
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userDAO.findByUsername("testuser")).thenReturn(userEntity);

        UserDetails userDetails = myUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        verify(userDAO, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userDAO.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> 
                myUserDetailsService.loadUserByUsername("nonexistent"));

        verify(userDAO, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsernameWithWhitespace() {
        when(userDAO.findByUsername("testuser")).thenReturn(userEntity);

        UserDetails userDetails = myUserDetailsService.loadUserByUsername("  testuser  ");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userDAO).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameWithUppercase() {
        when(userDAO.findByUsername("testuser")).thenReturn(userEntity);

        UserDetails userDetails = myUserDetailsService.loadUserByUsername("TESTUSER");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userDAO).findByUsername("testuser");
    }

    @Test
    void testUsernameNotFoundExceptionMessage() {
        when(userDAO.findByUsername("invalid")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                myUserDetailsService.loadUserByUsername("invalid"));

        assertTrue(exception.getMessage().contains("User not found with username: invalid"));
    }
}
