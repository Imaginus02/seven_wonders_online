package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.model.Ressources;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Manager responsible for handling wonder building mechanics.
 * Determines if a wonder stage can be built and executes the build action.
 */
@Component
public class WonderBuildManager {

    private final CardPlayManager cardPlayManager;

    public WonderBuildManager(CardPlayManager cardPlayManager) {
        this.cardPlayManager = cardPlayManager;
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
        System.out.println("[WonderBuildManager.buildWonderWithCard] Player: " + playerState.getUser().getUsername() + ", Card: " + cardToPlay.getName() + ", Current wonder stage: " + playerState.getWonderStage() + ", Wonder: " + playerState.getWonder().getName());
        if (canBuildWonderWithCard(playerState)) {
            Map<Ressources, Integer> wonderStageCost = playerState.getWonder().getStageCosts().get(playerState.getWonderStage());
            cardPlayManager.payCost(playerState, wonderStageCost);
            playerState.getHand().remove(cardToPlay);
            playerState.setWonderStage(playerState.getWonderStage() + 1);
            playerState.getWonderCards().add(cardToPlay);
            System.out.println("[WonderBuildManager.buildWonderWithCard] SUCCESS - Wonder stage built. New stage: " + playerState.getWonderStage() + ", Wonder cards: " + playerState.getWonderCards().size());
            // TODO: Apply wonder stage benefits
            return true;
        } else {
            System.out.println("[WonderBuildManager.buildWonderWithCard] FAILED - Cannot build wonder stage");
            return false;
        }
    }

    /**
     * Determines if a player can build the next wonder stage.
     * 
     * @param playerState the player state entity
     * @return true if the wonder stage can be built, false otherwise
     */
    public boolean canBuildWonderWithCard(PlayerStateEntity playerState) {
        WonderEntity wonder = playerState.getWonder();
        Integer wonderStage = playerState.getWonderStage();
        System.out.println("[WonderBuildManager.canBuildWonderWithCard] Player: " + playerState.getUser().getUsername() + ", Wonder: " + wonder.getName() + ", Current stage: " + wonderStage + ", Max stages: " + wonder.getNumberOfStages());

        if (wonderStage >= wonder.getNumberOfStages() - 1) {
            System.out.println("[WonderBuildManager.canBuildWonderWithCard] All stages already built");
            return false; // All stages already built
        }

        Map<Ressources, Integer> wonderStageCost = wonder.getStageCosts().get(wonderStage);
        return cardPlayManager.canAffordCost(playerState, wonderStageCost);
    }
}
