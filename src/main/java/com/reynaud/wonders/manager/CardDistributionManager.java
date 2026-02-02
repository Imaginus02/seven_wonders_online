package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.service.CardService;
import com.reynaud.wonders.service.LoggingService;
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
    private final LoggingService loggingService;

    public CardDistributionManager(PlayerStateService playerStateService, CardService cardService,
                                   LoggingService loggingService) {
        this.playerStateService = playerStateService;
        this.cardService = cardService;
        this.loggingService = loggingService;
    }

    /**
     * Distribute cards to all players for the current age.
     * Handles special logic for Age III (violet cards).
     * 
     * @param game the game entity containing the current age
     */
    @Transactional
    public void distributeCards(GameEntity game) {
        loggingService.info("Distributing cards - GameID: " + game.getId() + ", Age: " + game.getCurrentAge(), "CardDistributionManager.distributeCards");
        List<PlayerStateEntity> players = playerStateService.getPlayerStatesByGameId(game.getId());
        List<CardEntity> ageDeck = cardService.getCardsByAge(game.getCurrentAge());
        loggingService.debug("Initial deck size - DeckSize: " + ageDeck.size() + ", Players: " + players.size() + ", GameID: " + game.getId(), "CardDistributionManager.distributeCards");
        
        ageDeck = ageDeck.stream()
                .filter(card -> card.getMinPlayerCount() <= players.size())
                .collect(Collectors.toList());
        loggingService.debug("Deck size after player count filter - DeckSize: " + ageDeck.size() + ", GameID: " + game.getId(), "CardDistributionManager.distributeCards");

        if (game.getCurrentAge() == Age.AGE_III) {
            loggingService.debug("Age III detected - handling violet cards selection - GameID: " + game.getId(), "CardDistributionManager.distributeCards");
            
            ageDeck = ageDeck.stream()
                    .filter(card -> card.getType() != CardType.VIOLET)
                    .collect(Collectors.toList());
            
            List<CardEntity> violetCards = ageDeck.stream()
                    .filter(card -> card.getType() == CardType.VIOLET)
                    .collect(Collectors.toList());
            Collections.shuffle(violetCards);
            violetCards = violetCards.subList(0, ageDeck.size() - (players.size() * 7));
            loggingService.debug("Violet cards selected - Count: " + violetCards.size() + ", GameID: " + game.getId(), "CardDistributionManager.distributeCards");
            
            
            ageDeck.addAll(violetCards);
            loggingService.debug("Deck size after violet cards handling - DeckSize: " + ageDeck.size() + ", GameID: " + game.getId(), "CardDistributionManager.distributeCards");
        }
        
        int expectedTotalCards = players.size() * 7;
        loggingService.debug("Final deck size validation - FinalDeckSize: " + ageDeck.size() + ", ExpectedCards: " + expectedTotalCards + ", Players: " + players.size() + ", GameID: " + game.getId(), "CardDistributionManager.distributeCards");
        
        if (ageDeck.size() != expectedTotalCards) {
            loggingService.error("Invalid deck size for card distribution - DeckSize: " + ageDeck.size() + ", ExpectedCards: " + expectedTotalCards + ", Difference: " + (ageDeck.size() - expectedTotalCards) + ", Players: " + players.size() + ", Age: " + game.getCurrentAge() + ", GameID: " + game.getId(), "CardDistributionManager.distributeCards");
            throw new IllegalStateException("Card distribution failed: expected " + expectedTotalCards + " cards but got " + ageDeck.size() + " cards (Age: " + game.getCurrentAge() + ", Players: " + players.size() + ", GameID: " + game.getId() + ")");
        }
        
        Collections.shuffle(ageDeck);
        loggingService.debug("Starting card distribution to players - GameID: " + game.getId(), "CardDistributionManager.distributeCards");
        int handSize = 7;
        for (int i = 0; i < players.size(); i++) {
            List<CardEntity> hand = ageDeck.subList(i * handSize, (i + 1) * handSize);
            players.get(i).setHand(new ArrayList<>(hand));
            loggingService.debug("Cards distributed to player - Player: " + players.get(i).getUser().getUsername() + ", CardsReceived: " + hand.size() + ", GameID: " + game.getId(), "CardDistributionManager.distributeCards");
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
        loggingService.info("Rotating hands - Direction: " + (clockwise ? "clockwise" : "counter-clockwise") + ", Players: " + (playerStates != null ? playerStates.size() : 0), "CardDistributionManager.rotateHands");
        if (playerStates == null || playerStates.size() <= 1) {
            loggingService.warning("Cannot rotate hands - Not enough players - PlayerCount: " + (playerStates != null ? playerStates.size() : 0), "CardDistributionManager.rotateHands");
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
            loggingService.debug("Hand rotation - FromPlayer: " + orderedPlayers.get(i).getUser().getUsername() + " (Position: " + i + "), ToPlayer: " + orderedPlayers.get(targetIndex).getUser().getUsername() + " (Position: " + targetIndex + ")", "CardDistributionManager.rotateHands");
        }

        for (PlayerStateEntity player : orderedPlayers) {
            playerStateService.updatePlayerState(player);
        }
    }
}
