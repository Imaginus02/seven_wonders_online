package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.GameDAO;
import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.model.Ressources;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardActionService {

    private final GameDAO gameDAO;
    private final PlayerStateService playerStateService;
    private final CardService cardService;

    public CardActionService(GameDAO gameDAO, PlayerStateService playerStateService, CardService cardService) {
        this.gameDAO = gameDAO;
        this.playerStateService = playerStateService;
        this.cardService = cardService;
    }

    /**
     * Processes a card play action, moving the specified card from the player's hand to their played cards.
     * 
     * @param playerState the player state entity performing the action
     * @param cardToPlay the card entity to be played
     * @return true if the card was successfully played, false if the player cannot afford the card cost
     */
    @Transactional
    public boolean playCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        if (canPlayCard(playerState, cardToPlay)) { // TODO: Check if player can actually play this card
            playerState.getHand().remove(cardToPlay);
            playerState.getPlayedCards().add(cardToPlay);
            return true;
        } else {
            return false;
        }
    }

    private boolean canPlayCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        if (cardToPlay.getCoinCost() == null) {
            return canAffordCost(playerState, cardToPlay.getCost());
        } else {
            return cardToPlay.getCoinCost() <= playerState.getCoins();
        }
    }

    private boolean canAffordCost(PlayerStateEntity playerState, Map<Ressources, Integer> cardCost) {
        Map<Ressources, Integer> playerRessources = playerState.getResources();

        // Step 1: Calculate missing resources after using player's own resources
        // (excluding special mutable resources for now)
        Map<Ressources, Integer> missingResources = cardCost.entrySet().stream()
                .filter(entry -> entry.getKey().isRessource())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.max(0, entry.getValue() - playerRessources.getOrDefault(entry.getKey(), 0))
                ));
        
        // Step 2: Apply mutable resources (wildcards) to cover missing resources
        // Base and advanced resources are tracked separately as they cannot convert between types
        int missingBaseResources = calculateMissingResourceCount(missingResources, true)
                - playerRessources.getOrDefault(Ressources.MUTABLE_BASE, 0);
        int missingAdvancedResources = calculateMissingResourceCount(missingResources, false)
                - playerRessources.getOrDefault(Ressources.MUTABLE_ADVANCED, 0);
        
        // If player can afford with own resources and mutable resources, card is playable
        if (missingBaseResources <= 0 && missingAdvancedResources <= 0) {
            return true;
        }

        // Step 3: Check if neighbors can provide the remaining missing resources
        // Player must buy any remaining resources from left and/or right neighbors
        PlayerStateEntity leftNeighbor = playerState.getLeftNeighbor();
        PlayerStateEntity rightNeighbor = playerState.getRightNeighbor();
        Map<Ressources, Integer> leftResources = leftNeighbor.getResources();
        Map<Ressources, Integer> rightResources = rightNeighbor.getResources();
        
        // For each missing resource, check if neighbors can collectively provide it
        for (Map.Entry<Ressources, Integer> entry : missingResources.entrySet()) {
            Ressources resource = entry.getKey();
            int amountNeeded = entry.getValue();
            
            if (amountNeeded <= 0) {
                continue;
            }
            
            int availableFromLeft = leftResources.getOrDefault(resource, 0);
            int availableFromRight = rightResources.getOrDefault(resource, 0);
            int totalAvailableFromNeighbors = availableFromLeft + availableFromRight;
            
            // If neighbors can provide this resource, reduce the missing count
            if (totalAvailableFromNeighbors >= amountNeeded) {
                if (resource.isBaseRessource()) {
                    missingBaseResources -= amountNeeded;
                } else if (resource.isAdvancedRessource()) {
                    missingAdvancedResources -= amountNeeded;
                }
            }
        }
        
        // Card is playable only if all resource requirements can be met
        return missingBaseResources <= 0 && missingAdvancedResources <= 0;
    }

    /**
     * Calculate the total count of missing resources for either base or advanced resources
     */
    private int calculateMissingResourceCount(Map<Ressources, Integer> missingResources, boolean isBaseResource) {
        return missingResources.entrySet().stream()
                .filter(entry -> isBaseResource ? entry.getKey().isBaseRessource() : entry.getKey().isAdvancedRessource())
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    /**
     * Processes a card build action, using the specified card to build a stage of the player's wonder.
     * If successful, the card is moved from hand to wonder cards and the wonder stage counter is incremented.
     * 
     * @param playerState the player state entity performing the action
     * @param cardToPlay the card entity to be used for building the wonder
     * @return true if the wonder stage was successfully built, false if the player cannot afford the stage cost
     *         or all wonder stages have already been completed
     */
    @Transactional
    public boolean buildWonderWithCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        if (canBuildWonderWithCard(playerState, cardToPlay)) { // TODO: Check if player can actually build the wonder stage with this card
            playerState.getHand().remove(cardToPlay);
            playerState.setWonderStage(playerState.getWonderStage() + 1);
            playerState.getWonderCards().add(cardToPlay);
            // TODO: Apply wonder stage benefits
            return true;
        } else{
            return false;
        }
    }

    public boolean canBuildWonderWithCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        WonderEntity wonder = playerState.getWonder();
        Integer wonderStage = playerState.getWonderStage();

        if (wonderStage >= wonder.getNumberOfStages()-1) { // wonderStage is 0-indexed while numberOfStages is count
            return false; // All stages already built
        }

        Map<Ressources, Integer> wonderStageCost = wonder.getStageCosts().get(wonderStage);
        
        if (!canAffordCost(playerState, wonderStageCost)) {
            return false; // Cannot afford wonder stage cost
        }
        
        return true;
    }

    /**
     * Processes a card discard action, moving the specified card from the player's hand to the game's discard pile.
     * The player receives 3 coins as compensation for discarding the card.
     * 
     * @param playerState the player state entity performing the action
     * @param cardToPlay the card entity to be discarded
     * @param game the game entity to which the discarded card is added
     */
    @Transactional
    public void discardCard(PlayerStateEntity playerState, CardEntity cardToPlay, GameEntity game) {
        playerState.getHand().remove(cardToPlay);
        game.getDiscard().add(cardToPlay);
        playerState.setCoins(playerState.getCoins() + 3);
        gameDAO.save(game);
    }

    /**
     * Handles game creation logic by distributing initial card hands to all players.
     * This is called when a new game is started to prepare the first round of card distribution.
     * 
     * @param game the game entity for which initial hands should be distributed
     */
    @Transactional
    public void handleGameCreation(GameEntity game) {
        distributeCards(game);
    }

    /**
     * Handles end of turn logic by checking if all players have completed their turns.
     * If all players have played, resets the turn flags and either discards the last remaining card
     * and distributes new hands (end of round), or advances to the next age and rotates hands (end of age).
     * 
     * @param game the game entity
     * @param gameId the unique identifier of the game
     * @param playerState the player state entity of the current player
     */
    @Transactional
    public void handleEndOfTurn(GameEntity game, Long gameId, PlayerStateEntity playerState) {
        int remainingCards = playerState.getHand().size();
        
        if (playerStateService.allPlayersHavePlayedThisTurn(gameId)) {
            // Reset for next turn
            for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                ps.setHasPlayedThisTurn(false);
            }
            
            if (remainingCards == 1) {
                // Last card of the round - discard it and distribute new hand
                for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                    game.getDiscard().add(playerState.getHand().remove(0));
                }
                game.setCurrentAge(Age.getNextAge(game.getCurrentAge()));
                gameDAO.save(game);
                if (game.getCurrentAge() == null) {
                    // Game over - all ages completed
                    // TODO: Implement end game scoring and logic
                    return;
                }
                distributeCards(game);
            } else if (remainingCards >= 2) {
                // Advance to next age and rotate hands
                game.setCurrentAge(Age.getNextAge(game.getCurrentAge()));
                boolean clockwise = game.getCurrentAge() == null
                        || game.getCurrentAge() == Age.AGE_I
                        || game.getCurrentAge() == Age.AGE_III;
                rotateHands(playerStateService.getPlayerStatesByGameId(gameId), clockwise);
            }
            for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                playerStateService.updatePlayerState(ps);
            }
        }
    }

    /**
     * Rotate player hands either clockwise or counter-clockwise
     */
    private void rotateHands(List<PlayerStateEntity> playerStates, boolean clockwise) {
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

    /**
     * Distribute cards when a round ends
     */
    private void distributeCards(GameEntity game) {
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
            violetCards = violetCards.subList(0, players.size()+2);
            
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

}
