package com.reynaud.wonders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.reynaud.wonders.service.GameService;
import com.reynaud.wonders.service.LoggingService;
import com.reynaud.wonders.service.PlayerStateService;
import com.reynaud.wonders.service.UserService;

//import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/play")
public class MainUIController {

    private final GameService gameService;
    private final UserService userService;
    private final PlayerStateService playerStateService;
    private final LoggingService loggingService;

    @Autowired
    public MainUIController(GameService gameService, UserService userService, PlayerStateService playerStateService, LoggingService loggingService) {
        this.gameService = gameService;
        this.userService = userService;
        this.playerStateService = playerStateService;
        this.loggingService = loggingService;
    }   

    @GetMapping
    public String showMainUI(
        @RequestParam Long gameId,
        HttpServletRequest request,
        Authentication authentication) {

    loggingService.debug("Showing main UI for gameId: " + gameId, "MainUIController.showMainUI");

    if (!gameService.doesGameExist(gameId)) {
        loggingService.warning("Game ID " + gameId + " does not exist. Redirecting to home.", "MainUIController.showMainUI");
        return "redirect:/home";
    }

    if (playerStateService.getPlayerStateByGameIdAndUserId(gameId, userService.findByUsername(authentication.getName()).getId()).getWonderName() == null) {
            loggingService.info("Player has not chosen wonder side for gameId: " + gameId + ". Redirecting to choose-side.", "MainUIController.showMainUI");
            return "redirect:/choose-side?gameId=" + gameId;
        }

        return "mainui";
    }

    @GetMapping("{gameId}")
    public String showMainUIPathVariable(
        @PathVariable Long gameId,
        HttpServletRequest request,
        Authentication authentication) {

    loggingService.debug("Showing main UI for gameId: " + gameId, "MainUIController.showMainUIPathVariable");

    if (!gameService.doesGameExist(gameId)) {
        loggingService.warning("Game ID " + gameId + " does not exist. Redirecting to home.", "MainUIController.showMainUIPathVariable");
        return "redirect:/home";
    }

    if (playerStateService.getPlayerStateByGameIdAndUserId(gameId, userService.findByUsername(authentication.getName()).getId()).getWonderName() == null) {
            loggingService.info("Player has not chosen wonder side for gameId: " + gameId + ". Redirecting to choose-side.", "MainUIController.showMainUIPathVariable");
            return "redirect:/choose-side?gameId=" + gameId;
        }

        return "mainui";
    }
}
