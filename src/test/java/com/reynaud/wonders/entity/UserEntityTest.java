package com.reynaud.wonders.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

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
    void testIdGetterSetter() {
        userEntity.setId(2L);
        assertEquals(2L, userEntity.getId());
    }

    @Test
    void testUsernameGetterSetter() {
        userEntity.setUsername("newuser");
        assertEquals("newuser", userEntity.getUsername());
    }

    @Test
    void testPasswordGetterSetter() {
        userEntity.setPassword("newPassword");
        assertEquals("newPassword", userEntity.getPassword());
    }

    @Test
    void testRoleGetterSetter() {
        userEntity.setRole("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", userEntity.getRole());
    }

    @Test
    void testDefaultRole() {
        UserEntity newUser = new UserEntity();
        assertEquals("ROLE_USER", newUser.getRole());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userEntity.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testGetAuthoritiesWithAdminRole() {
        userEntity.setRole("ROLE_ADMIN");
        Collection<? extends GrantedAuthority> authorities = userEntity.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(userEntity.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(userEntity.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userEntity.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(userEntity.isEnabled());
    }

    @Test
    void testUserDetailsContract() {
        // Ensure the user entity implements UserDetails correctly
        assertTrue(userEntity.isAccountNonExpired());
        assertTrue(userEntity.isAccountNonLocked());
        assertTrue(userEntity.isCredentialsNonExpired());
        assertTrue(userEntity.isEnabled());
        assertNotNull(userEntity.getUsername());
        assertNotNull(userEntity.getPassword());
        assertNotNull(userEntity.getAuthorities());
    }
}
