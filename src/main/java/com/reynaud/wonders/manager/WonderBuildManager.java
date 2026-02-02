package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.model.Ressources;
import com.reynaud.wonders.service.LoggingService;
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
    private final LoggingService loggingService;

    public WonderBuildManager(CardPlayManager cardPlayManager, LoggingService loggingService) {
        this.cardPlayManager = cardPlayManager;
        this.loggingService = loggingService;
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
        loggingService.debug("Building wonder with card - Player: " + playerState.getUser().getUsername() + ", Card: " + cardToPlay.getName() + ", CurrentStage: " + playerState.getWonderStage() + ", Wonder: " + playerState.getWonder().getName(), "WonderBuildManager.buildWonderWithCard");
        if (canBuildWonderWithCard(playerState)) {
            Map<Ressources, Integer> wonderStageCost = playerState.getWonder().getStageCosts().get(playerState.getWonderStage());
            cardPlayManager.payCost(playerState, wonderStageCost);
            playerState.getHand().remove(cardToPlay);
            playerState.setWonderStage(playerState.getWonderStage() + 1);
            playerState.getWonderCards().add(cardToPlay);
            loggingService.info("Wonder stage built successfully - Player: " + playerState.getUser().getUsername() + ", NewStage: " + playerState.getWonderStage() + ", WonderCards: " + playerState.getWonderCards().size() + ", Wonder: " + playerState.getWonder().getName(), "WonderBuildManager.buildWonderWithCard");
            // TODO: Apply wonder stage benefits
            return true;
        } else {
            loggingService.warning("Cannot build wonder stage - Player: " + playerState.getUser().getUsername() + ", Wonder: " + playerState.getWonder().getName() + ", Stage: " + playerState.getWonderStage(), "WonderBuildManager.buildWonderWithCard");
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
        loggingService.debug("Checking if wonder can be built - Player: " + playerState.getUser().getUsername() + ", Wonder: " + wonder.getName() + ", CurrentStage: " + wonderStage + ", MaxStages: " + wonder.getNumberOfStages(), "WonderBuildManager.canBuildWonderWithCard");

        if (wonderStage >= wonder.getNumberOfStages() - 1) {
            loggingService.debug("All wonder stages already built - Player: " + playerState.getUser().getUsername() + ", Wonder: " + wonder.getName(), "WonderBuildManager.canBuildWonderWithCard");
            return false; // All stages already built
        }

        Map<Ressources, Integer> wonderStageCost = wonder.getStageCosts().get(wonderStage);
        return cardPlayManager.canAffordCost(playerState, wonderStageCost);
    }
}
