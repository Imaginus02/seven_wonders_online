package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.model.Ressources;
import com.reynaud.wonders.model.Science;
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

            applyWonderStageBenefits(playerState);
            loggingService.info("Wonder stage built successfully - Player: " + playerState.getUser().getUsername() + ", NewStage: " + playerState.getWonderStage() + ", WonderCards: " + playerState.getWonderCards().size() + ", Wonder: " + playerState.getWonder().getName(), "WonderBuildManager.buildWonderWithCard");
            return true;
        } else {
            loggingService.warning("Cannot build wonder stage - Player: " + playerState.getUser().getUsername() + ", Wonder: " + playerState.getWonder().getName() + ", Stage: " + playerState.getWonderStage(), "WonderBuildManager.buildWonderWithCard");
            return false;
        }
    }

    private void applyWonderStageBenefits(PlayerStateEntity playerState) {
        switch (playerState.getWonder().getName()) {
            case "Alexandria":
                switch (playerState.getWonder().getFace()) {
                    case "A":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                playerState.getResources().merge(Ressources.MUTABLE_BASE, 1, Integer::sum);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                    case "B":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.getResources().merge(Ressources.MUTABLE_BASE, 1, Integer::sum);
                                break;
                            case 2:
                                playerState.getResources().merge(Ressources.MUTABLE_ADVANCED, 1, Integer::sum);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                }
                break;
            case "Babylon":
                switch (playerState.getWonder().getFace()) {
                    case "A":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                playerState.getScience().merge(Science.MUTABLE, 1, Integer::sum);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                    case "B":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                //TODO: Handle playing the last two cards of the age at the end of the age
                                loggingService.warning("Babylon B stage 2 benefit not implemented - Player: " + playerState.getUser().getUsername(), "WonderBuildManager.applyWonderStageBenefits");
                                break;
                            case 3:
                                playerState.getScience().merge(Science.MUTABLE, 1, Integer::sum);
                                break;
                        }
                        break;
                }
                break;
            case "Ephesos":
                switch (playerState.getWonder().getFace()) {
                    case "A":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                playerState.setCoins(playerState.getCoins()+9);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                    case "B":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+2);
                                playerState.setCoins(playerState.getCoins()+4);
                                break;
                            case 2:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                playerState.setCoins(playerState.getCoins()+4);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+5);
                                playerState.setCoins(playerState.getCoins()+4);
                                break;
                        }
                        break;
                }
                break;
            case "Gizah":
                switch (playerState.getWonder().getFace()) {
                    case "A":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+5);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                    case "B":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+5);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+5);
                                break;
                            case 4:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                }
                break;
            case "Halikarnassos":
                switch (playerState.getWonder().getFace()) {
                    case "A":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                //TODO: Handle building a card freely from discard pile
                                loggingService.warning("Halikarnassos A stage 2 benefit not implemented - Player: " + playerState.getUser().getUsername(), "WonderBuildManager.applyWonderStageBenefits");
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                    case "B":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                //TODO: Handle building a card freely from discard pile
                                loggingService.warning("Halikarnassos B stage 1 benefit not implemented - Player: " + playerState.getUser().getUsername(), "WonderBuildManager.applyWonderStageBenefits");
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+2);
                                break;
                            case 2:
                                //TODO: Handle building a card freely from discard pile
                                loggingService.warning("Halikarnassos B stage 2 benefit not implemented - Player: " + playerState.getUser().getUsername(), "WonderBuildManager.applyWonderStageBenefits");
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+1);
                                break;
                            case 3:
                                //TODO: Handle building a card freely from discard pile
                                loggingService.warning("Halikarnassos B stage 3 benefit not implemented - Player: " + playerState.getUser().getUsername(), "WonderBuildManager.applyWonderStageBenefits");
                                break;
                        }
                        break;
                }
                break;
            case "Olympia":
                switch (playerState.getWonder().getFace()) {
                    case "A":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                //TODO: Handle first card from each age is free
                                loggingService.warning("Olympia A stage 2 benefit not implemented - Player: " + playerState.getUser().getUsername(), "WonderBuildManager.applyWonderStageBenefits");
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                    case "B":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setLeftBaseRessourcePriceMultiplier(1);
                                playerState.setRightBaseRessourcePriceMultiplier(1);
                                break;
                            case 2:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+5);
                                break;
                            case 3:
                                //TODO: Handle copying a violet card from a neighbor at the end of the game
                                loggingService.warning("Olympia B stage 3 benefit not implemented - Player: " + playerState.getUser().getUsername(), "WonderBuildManager.applyWonderStageBenefits");
                                break;
                        }
                        break;
                }
                break;
            case "Rhodes":
                switch (playerState.getWonder().getFace()) {
                    case "A":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                break;
                            case 2:
                                playerState.setMilitaryPoints(playerState.getMilitaryPoints()+2);
                                break;
                            case 3:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+7);
                                break;
                        }
                        break;
                    case "B":
                        switch (playerState.getWonderStage()) {
                            case 1:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+3);
                                playerState.setCoins(playerState.getCoins()+3);
                                playerState.setMilitaryPoints(playerState.getMilitaryPoints()+1);
                                break;
                            case 2:
                                playerState.setVictoryPoints(playerState.getVictoryPoints()+4);
                                playerState.setCoins(playerState.getCoins()+4);
                                playerState.setMilitaryPoints(playerState.getMilitaryPoints()+1);
                                break;
                        }
                        break;
                }
                break;
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
