package com.reynaud.wonders.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.service.PlayerStateService;
import com.reynaud.wonders.service.UserService;
import com.reynaud.wonders.service.WonderService;

@Controller
@RequestMapping("/choose-side")
public class WonderDeciderController {

    private final UserService userService;
    private final PlayerStateService playerStateService;
    private final WonderService wonderService;
    

    public WonderDeciderController(PlayerStateService playerStateService, UserService userService, WonderService wonderService) {
        this.playerStateService = playerStateService;
        this.userService = userService;
        this.wonderService = wonderService;
    }

    @GetMapping
    public String chooseWonderSidePage(@RequestParam Long gameId, Authentication authentication, Model model) {
        return showChooseWonderSide(gameId, authentication, model);
    }

    @GetMapping("{gameId}")
    public String chooseWonderSidePagePath(@PathVariable Long gameId, Authentication authentication, Model model) {
        return showChooseWonderSide(gameId, authentication, model);
    }

    @PostMapping
    public String chooseWonderSide(
            @RequestParam Long gameId,
            @RequestParam Long wonderId,
            Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return "redirect:/home";
        }
        if (playerState.getWonderName() != null) {
            return "redirect:/play?gameId=" + gameId;
        }

        WonderEntity selectedWonder = wonderService.getWonderById(wonderId);
        if (selectedWonder == null || playerState.getWonder() == null
                || !selectedWonder.getName().equals(playerState.getWonder().getName())) {
            return "redirect:/choose-side?gameId=" + gameId;
        }

        playerState.setWonder(selectedWonder);
        playerState.setWonderName(selectedWonder.getName());
        playerState.setWonderStage(0);
        playerState.setResources(selectedWonder.getStartingResources());
        playerStateService.updatePlayerState(playerState);

        return "redirect:/play?gameId=" + gameId;
    }

    private String showChooseWonderSide(Long gameId, Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return "redirect:/home";
        }
        if (playerState.getWonderName() != null) {
            return "redirect:/play?gameId=" + gameId;
        }

        List<WonderEntity> wonders = wonderService.getWondersByName(playerState.getWonder().getName());
        model.addAttribute("wonders", wonders);
        model.addAttribute("gameId", gameId);
        model.addAttribute("wonderName", playerState.getWonder().getName());
        return "choose_wonder_side";
    }
}
