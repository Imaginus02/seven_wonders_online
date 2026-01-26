package com.reynaud.wonders;

import com.reynaud.wonders.dto.UserDTO;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RegistrationControllerTest {

    private RegistrationController registrationController;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        registrationController = new RegistrationController(userService);
    }

    @Test
    void testShowRegistrationForm() {
        Model model = mock(Model.class);
        
        String result = registrationController.showRegistrationForm(model);
        
        assertEquals("register", result);
        verify(model).addAttribute(eq("userDTO"), any(UserDTO.class));
    }

    @Test
    void testRegisterUserSuccess() {
        UserEntity newUser = new UserEntity();
        newUser.setId(1L);
        newUser.setUsername("testuser");

        when(userService.registerUser(any(UserDTO.class))).thenReturn(newUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("SecurePass123");
        userDTO.setConfirmPassword("SecurePass123");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = registrationController.registerUser(userDTO, bindingResult, redirectAttributes, model);
        
        assertEquals("redirect:/login", result);
        verify(userService).registerUser(any(UserDTO.class));
    }

    @Test
    void testRegisterUserWithValidationErrors() {
        UserDTO userDTO = new UserDTO();
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = registrationController.registerUser(userDTO, bindingResult, redirectAttributes, model);
        
        assertEquals("register", result);
        verify(model).addAttribute("error", "Please fix the highlighted fields");
        verify(userService, never()).registerUser(any(UserDTO.class));
    }

    @Test
    void testRegisterUserWithMismatchedPasswords() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("SecurePass123");
        userDTO.setConfirmPassword("DifferentPass123");
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = registrationController.registerUser(userDTO, bindingResult, redirectAttributes, model);
        
        assertEquals("register", result);
        verify(model).addAttribute("error", "Passwords do not match");
        verify(userService, never()).registerUser(any(UserDTO.class));
    }

    @Test
    void testRegisterUserWithUsernameAlreadyExists() {
        when(userService.registerUser(any(UserDTO.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existinguser");
        userDTO.setPassword("SecurePass123");
        userDTO.setConfirmPassword("SecurePass123");
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = registrationController.registerUser(userDTO, bindingResult, redirectAttributes, model);
        
        assertEquals("register", result);
        verify(model).addAttribute("error", "Username already exists");
    }

    @Test
    void testRegisterUserWithUnexpectedException() {
        when(userService.registerUser(any(UserDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("SecurePass123");
        userDTO.setConfirmPassword("SecurePass123");
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Model model = mock(Model.class);

        String result = registrationController.registerUser(userDTO, bindingResult, redirectAttributes, model);
        
        assertEquals("register", result);
        verify(model).addAttribute("error", "An error occurred during registration. Please try again.");
    }
}