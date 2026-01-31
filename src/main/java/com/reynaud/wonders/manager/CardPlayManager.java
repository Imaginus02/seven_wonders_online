package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.Ressources;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager responsible for handling card play mechanics.
 * Determines if a card can be played and executes the play action.
 */
@Component
public class CardPlayManager {

    /**
     * Processes a card play action, moving the specified card from the player's hand to their played cards.
     * 
     * @param playerState the player state entity performing the action
     * @param cardToPlay the card entity to be played
     * @return true if the card was successfully played, false if the player cannot afford the card cost
     */
    @Transactional
    public boolean playCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        if (canPlayCard(playerState, cardToPlay)) {
            playerState.getHand().remove(cardToPlay);
            playerState.getPlayedCards().add(cardToPlay);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if a player can play a given card based on cost affordability.
     * 
     * @param playerState the player state entity
     * @param cardToPlay the card to check
     * @return true if the player can afford the card cost, false otherwise
     */
    public boolean canPlayCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        if (cardToPlay.getCoinCost() == null) {
            return canAffordCost(playerState, cardToPlay.getCost());
        } else {
            return cardToPlay.getCoinCost() <= playerState.getCoins();
        }
    }

    /**
     * Determines if a player can afford a resource-based cost.
     * Considers the player's own resources, mutable resources (wildcards), and neighbor resources.
     * 
     * @param playerState the player state entity
     * @param cardCost the cost map of resources needed
     * @return true if the cost can be afforded, false otherwise
     */
    private boolean canAffordCost(PlayerStateEntity playerState, Map<Ressources, Integer> cardCost) {
        Map<Ressources, Integer> playerRessources = playerState.getResources();

        // Step 1: Calculate missing resources after using player's own resources
        Map<Ressources, Integer> missingResources = cardCost.entrySet().stream()
                .filter(entry -> entry.getKey().isRessource())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.max(0, entry.getValue() - playerRessources.getOrDefault(entry.getKey(), 0))
                ));
        
        // Step 2: Apply mutable resources (wildcards) to cover missing resources
        int missingBaseResources = calculateMissingResourceCount(missingResources, true)
                - playerRessources.getOrDefault(Ressources.MUTABLE_BASE, 0);
        int missingAdvancedResources = calculateMissingResourceCount(missingResources, false)
                - playerRessources.getOrDefault(Ressources.MUTABLE_ADVANCED, 0);
        
        if (missingBaseResources <= 0 && missingAdvancedResources <= 0) {
            return true;
        }

        // Step 3: Check if neighbors can provide the remaining missing resources
        PlayerStateEntity leftNeighbor = playerState.getLeftNeighbor();
        PlayerStateEntity rightNeighbor = playerState.getRightNeighbor();
        Map<Ressources, Integer> leftResources = leftNeighbor.getResources();
        Map<Ressources, Integer> rightResources = rightNeighbor.getResources();
        
        for (Map.Entry<Ressources, Integer> entry : missingResources.entrySet()) {
            Ressources resource = entry.getKey();
            int amountNeeded = entry.getValue();
            
            if (amountNeeded <= 0) {
                continue;
            }
            
            int availableFromLeft = leftResources.getOrDefault(resource, 0);
            int availableFromRight = rightResources.getOrDefault(resource, 0);
            int totalAvailableFromNeighbors = availableFromLeft + availableFromRight;
            
            if (totalAvailableFromNeighbors >= amountNeeded) {
                if (resource.isBaseRessource()) {
                    missingBaseResources -= amountNeeded;
                } else if (resource.isAdvancedRessource()) {
                    missingAdvancedResources -= amountNeeded;
                }
            }
        }
        
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
     * Processes a card discard action, moving the specified card from the player's hand to the game's discard pile.
     * The player receives 3 coins as compensation for discarding the card.
     * Note: Caller is responsible for persisting the game entity changes.
     * 
     * @param playerState the player state entity performing the action
     * @param cardToDiscard the card entity to be discarded
     * @param gameDiscard the game's discard pile to add the card to
     */
    @Transactional
    public void discardCard(PlayerStateEntity playerState, CardEntity cardToDiscard, java.util.List<CardEntity> gameDiscard) {
        playerState.getHand().remove(cardToDiscard);
        gameDiscard.add(cardToDiscard);
        playerState.setCoins(playerState.getCoins() + 3);
    }
}
