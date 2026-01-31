package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.service.CardService;
import com.reynaud.wonders.service.PlayerStateService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager responsible for managing card distribution and hand rotation.
 * Handles initial card distribution and rotation of hands between ages.
 */
@Component
public class CardDistributionManager {

    private final PlayerStateService playerStateService;
    private final CardService cardService;

    public CardDistributionManager(PlayerStateService playerStateService, CardService cardService) {
        this.playerStateService = playerStateService;
        this.cardService = cardService;
    }

    /**
     * Distribute cards to all players for the current age.
     * Handles special logic for Age III (violet cards).
     * 
     * @param game the game entity containing the current age
     */
    @Transactional
    public void distributeCards(GameEntity game) {
        List<PlayerStateEntity> players = playerStateService.getPlayerStatesByGameId(game.getId());
        List<CardEntity> ageDeck = cardService.getCardsByAge(game.getCurrentAge());
        int handSize = ageDeck.size() / players.size();
        ageDeck = ageDeck.stream()
                .filter(card -> card.getMinPlayerCount() <= players.size())
                .collect(Collectors.toList());

        if (game.getCurrentAge() == Age.AGE_III) {
            List<CardEntity> violetCards = ageDeck.stream()
                    .filter(card -> card.getType() == CardType.VIOLET)
                    .collect(Collectors.toList());
            Collections.shuffle(violetCards);
            violetCards = violetCards.subList(0, players.size() + 2);
            
            ageDeck = ageDeck.stream()
                    .filter(card -> card.getType() != CardType.VIOLET || card.getMinPlayerCount() <= players.size())
                    .collect(Collectors.toList());
            ageDeck.addAll(violetCards);
        }
        
        Collections.shuffle(ageDeck);
        for (int i = 0; i < players.size(); i++) {
            List<CardEntity> hand = ageDeck.subList(i * handSize, (i + 1) * handSize);
            players.get(i).setHand(new ArrayList<>(hand));
            playerStateService.updatePlayerState(players.get(i));
        }
    }

    /**
     * Rotate player hands either clockwise or counter-clockwise.
     * Used between ages to pass cards to the next player in sequence.
     * 
     * @param playerStates list of player states in the game
     * @param clockwise true to rotate clockwise, false to rotate counter-clockwise
     */
    @Transactional
    public void rotateHands(List<PlayerStateEntity> playerStates, boolean clockwise) {
        if (playerStates == null || playerStates.size() <= 1) {
            return;
        }

        List<PlayerStateEntity> orderedPlayers = playerStates.stream()
                .sorted(Comparator.comparing(PlayerStateEntity::getPosition))
                .collect(Collectors.toList());

        List<List<CardEntity>> hands = orderedPlayers.stream()
                .map(player -> new ArrayList<>(player.getHand()))
                .collect(Collectors.toList());

        int playerCount = orderedPlayers.size();
        for (int i = 0; i < playerCount; i++) {
            int targetIndex = clockwise ? (i + 1) % playerCount : (i - 1 + playerCount) % playerCount;
            orderedPlayers.get(targetIndex).setHand(hands.get(i));
        }

        for (PlayerStateEntity player : orderedPlayers) {
            playerStateService.updatePlayerState(player);
        }
    }
}
