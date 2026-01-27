package com.reynaud.wonders.controller;

import com.reynaud.wonders.entity.Age;
import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.CardType;
import com.reynaud.wonders.service.CardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public String viewCards(Model model) {
        Map<Age, Map<CardType, java.util.List<CardEntity>>> cardsByAgeAndType = cardService.getCardsByAgeAndType();
        model.addAttribute("cardsByAgeAndType", cardsByAgeAndType);
        return "cards";
    }
}
