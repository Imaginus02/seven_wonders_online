package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.CardDAO;
import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.CardType;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardDAO cardDAO;

    public CardService(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    /**
     * Get all cards organized by Age, and within each Age, by CardType (color).
     * Returns a Map<Age, Map<CardType, List<CardEntity>>>
     */
    public Map<Age, Map<CardType, List<CardEntity>>> getCardsByAgeAndType() {
        List<CardEntity> allCards = cardDAO.findAll();
        
        return allCards.stream()
                .collect(Collectors.groupingBy(
                        CardEntity::getAge,
                        Collectors.groupingBy(
                                CardEntity::getType,
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().ordinal()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    /**
     * Get all cards
     */
    public List<CardEntity> getAllCards() {
        return cardDAO.findAll();
    }

    /**
     * Get card by ID
     */
    public CardEntity getCardById(Long id) {
        return cardDAO.findById(id).orElse(null);
    }

    /**
     * Get cards by IDs
     */
    public List<CardEntity> getCardsByIds(List<Long> ids) {
        return cardDAO.findAllById(ids);
    }

    /**
     * Get cards by Age
     */
    public List<CardEntity> getCardsByAge(Age age) {
        return cardDAO.findByAge(age);
    }
}
