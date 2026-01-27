package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.CardDAO;
import com.reynaud.wonders.entity.Age;
import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.CardType;
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
}
