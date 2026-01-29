package com.reynaud.wonders.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/play")
public class MainUIController {

    @GetMapping
    public String showMainUI() {
        return "mainui";
    }
}
