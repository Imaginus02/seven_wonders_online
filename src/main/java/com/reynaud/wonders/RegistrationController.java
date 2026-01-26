package com.reynaud.wonders;

import com.reynaud.wonders.dto.UserDTO;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
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
            log.info("Registered user id={} username={}", registeredUser.getId(), registeredUser.getUsername());
            // Redirect to login page with success message
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Please log in.");
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            log.warn("Registration validation failed for username={}: {}", userDTO.getUsername(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            log.error("Unexpected error during registration for username={}", userDTO.getUsername(), e);
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            return "register";
        }
    }
}
