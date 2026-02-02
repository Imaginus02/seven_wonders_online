package com.reynaud.wonders.controller;

import com.reynaud.wonders.service.LoggingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final LoggingService loggingService;

    public HomeController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        loggingService.debug("Accessing home page - User: " + (authentication != null ? authentication.getName() : "not authenticated"), "HomeController.home");
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            // Check if user is admin
            boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
        return "home";
    }
}
