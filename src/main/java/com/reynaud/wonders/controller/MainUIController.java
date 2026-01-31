package com.reynaud.wonders.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/play")
public class MainUIController {

    @GetMapping
    public String showMainUI(HttpServletRequest request, Model model) {
        //CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        //if (token != null) {
        //    model.addAttribute("_csrf", token);
        //}
        return "mainui";
    }
}
