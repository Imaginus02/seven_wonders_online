package com.reynaud.wonders.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
//TODO: import this in prod
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurity {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SpringSecurity(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .headers((headers) -> headers.frameOptions(frame -> frame.sameOrigin()))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/register", "/login", "/h2-console/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider())
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //TODO: switch to BCryptPasswordEncoder()
        return org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}