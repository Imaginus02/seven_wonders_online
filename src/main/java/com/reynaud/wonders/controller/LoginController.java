package com.reynaud.wonders.controller;

import com.reynaud.wonders.service.LoggingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final LoggingService loggingService;

    public LoginController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @GetMapping("/login")
    public String login() {
        loggingService.debug("Accessing login page", "LoginController.login");
        return "login"; // returns the login.html view
    }
}