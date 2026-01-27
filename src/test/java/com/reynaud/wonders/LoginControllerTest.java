package com.reynaud.wonders;

import org.junit.jupiter.api.Test;

import com.reynaud.wonders.controller.LoginController;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController = new LoginController();

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
