package com.reynaud.wonders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.reynaud.wonders.service.GameService;

//import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/play")
public class MainUIController {

    private final GameService gameService;

    @Autowired
    public MainUIController(GameService gameService) {
        this.gameService = gameService;
    }   

    @GetMapping
    public String showMainUI(
        @RequestParam Long gameId,
        HttpServletRequest request) {

    if (!gameService.doesGameExist(gameId)) {
        return "redirect:/home";
    }

        return "mainui";
    }
}
