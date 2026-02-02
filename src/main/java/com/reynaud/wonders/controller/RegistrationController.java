package com.reynaud.wonders.controller;

import com.reynaud.wonders.dto.UserDTO;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.service.LoggingService;
import com.reynaud.wonders.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final LoggingService loggingService;

    public RegistrationController(UserService userService, LoggingService loggingService) {
        this.userService = userService;
        this.loggingService = loggingService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        loggingService.debug("Accessing registration page", "RegistrationController.showRegistrationForm");
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("error", "Please fix the highlighted fields");
                return "register";
            }

            if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
                model.addAttribute("error", "Passwords do not match");
                return "register";
            }

            // Register the user
            UserEntity registeredUser = userService.registerUser(userDTO);
            loggingService.info("User registered successfully - UserID: " + registeredUser.getId() + ", Username: " + registeredUser.getUsername(), "RegistrationController.registerUser");
            // Redirect to login page with success message
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please log in.");
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            loggingService.warning("Registration validation failed - Username: " + userDTO.getUsername() + ", Error: " + e.getMessage(), "RegistrationController.registerUser");
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            loggingService.error("Unexpected error during registration - Username: " + userDTO.getUsername() + ", Error: " + e.getMessage(), "RegistrationController.registerUser", e);
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            return "register";
        }
    }
}
