package com.reynaud.wonders;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    private HomeController homeController = new HomeController();

    @Test
    void testHomeWithAuthenticatedUser() {
        Model model = mock(Model.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        
        String result = homeController.home(authentication, model);
        
        assertEquals("home", result);
        verify(model).addAttribute("username", "testuser");
    }

    @Test
    void testHomeWithAuthenticatedAdminUser() {
        Model model = mock(Model.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        
        String result = homeController.home(authentication, model);
        
        assertEquals("home", result);
        verify(model).addAttribute("username", "admin");
    }

    @Test
    void testHomeWithNullAuthentication() {
        Model model = mock(Model.class);
        
        String result = homeController.home(null, model);
        
        assertEquals("home", result);
        verify(model, never()).addAttribute(anyString(), any());
    }
}
