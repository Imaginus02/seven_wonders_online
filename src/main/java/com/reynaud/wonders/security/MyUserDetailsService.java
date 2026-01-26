package com.reynaud.wonders.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.reynaud.wonders.dao.UserDAO;
import com.reynaud.wonders.entity.UserEntity;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    public MyUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        UserEntity user = userDAO.findByUsername(normalizedUsername);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }
    
}
