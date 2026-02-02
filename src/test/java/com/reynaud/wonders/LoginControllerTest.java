package com.reynaud.wonders;

import org.junit.jupiter.api.Test;

import com.reynaud.wonders.controller.LoginController;
import com.reynaud.wonders.service.LoggingService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private LoggingService loggingService = mock(LoggingService.class);
    private LoginController loginController = new LoginController(loggingService);

    @Test
    void testLoginPageDisplays() {
        String result = loginController.login();
        assertEquals("login", result);
    }

    @Test
    void testLoginPageReturnsCorrectView() {
        String viewName = loginController.login();
        assertNotNull(viewName);
        assertEquals("login", viewName);
    }
}
