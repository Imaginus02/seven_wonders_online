package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.model.Ressources;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager responsible for handling wonder building mechanics.
 * Determines if a wonder stage can be built and executes the build action.
 */
@Component
public class WonderBuildManager {

    public WonderBuildManager() {
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
        if (canBuildWonderWithCard(playerState, cardToPlay)) {
            playerState.getHand().remove(cardToPlay);
            playerState.setWonderStage(playerState.getWonderStage() + 1);
            playerState.getWonderCards().add(cardToPlay);
            // TODO: Apply wonder stage benefits
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if a player can build the next wonder stage with a given card.
     * 
     * @param playerState the player state entity
     * @param cardToPlay the card to use for building
     * @return true if the wonder stage can be built, false otherwise
     */
    public boolean canBuildWonderWithCard(PlayerStateEntity playerState, CardEntity cardToPlay) {
        WonderEntity wonder = playerState.getWonder();
        Integer wonderStage = playerState.getWonderStage();

        if (wonderStage >= wonder.getNumberOfStages() - 1) {
            return false; // All stages already built
        }

        Map<Ressources, Integer> wonderStageCost = wonder.getStageCosts().get(wonderStage);
        return canAffordWonderStageCost(playerState, wonderStageCost);
    }

    /**
     * Determines if a player can afford the resource cost of a wonder stage.
     * Uses the same affordability logic as card play (via CardPlayManager).
     * 
     * @param playerState the player state entity
     * @param wonderStageCost the cost map of resources needed
     * @return true if the cost can be afforded, false otherwise
     */
    private boolean canAffordWonderStageCost(PlayerStateEntity playerState, Map<Ressources, Integer> wonderStageCost) {
        Map<Ressources, Integer> playerRessources = playerState.getResources();

        // Step 1: Calculate missing resources after using player's own resources
        Map<Ressources, Integer> missingResources = wonderStageCost.entrySet().stream()
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
}
