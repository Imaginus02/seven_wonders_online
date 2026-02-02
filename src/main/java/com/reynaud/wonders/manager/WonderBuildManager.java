package com.reynaud.wonders.manager;

import com.reynaud.wonders.dao.EffectDAO;
import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.EffectEntity;
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

    //TODO: DAO shouldn't be accessed by manager, use service instead
    private final EffectDAO effectDAO;

    public WonderBuildManager(CardPlayManager cardPlayManager, LoggingService loggingService, 
                             EffectDAO effectDAO) {
        this.cardPlayManager = cardPlayManager;
        this.loggingService = loggingService;
        this.effectDAO = effectDAO;
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

    /**
     * Applies the benefits of building a wonder stage using the effect system.
     * Maps wonder name, face, and stage to effect ID and applies via EffectExecutorService.
     * 
     * @param playerState the player who built the stage
     */
    private void applyWonderStageBenefits(PlayerStateEntity playerState) {
        String wonderName = playerState.getWonder().getName();
        String face = playerState.getWonder().getFace();
        int stage = playerState.getWonderStage();
        
        String effectId = mapWonderStageToEffectId(wonderName, face, stage);
        
        if (effectId == null) {
            loggingService.warning("No effect mapping for wonder stage - Wonder: " + wonderName + 
                                 ", Face: " + face + ", Stage: " + stage, "WonderBuildManager.applyWonderStageBenefits");
            return;
        }
        
        EffectEntity effect = effectDAO.findByEffectId(effectId).orElse(null);
        if (effect == null) {
            loggingService.error("Effect not found: " + effectId + " for wonder: " + wonderName + 
                               " " + face + " stage " + stage, "WonderBuildManager.applyWonderStageBenefits");
            return;
        }

        playerState.addPendingEffect(effect);
        loggingService.debug("Added pending effect - Player: " + playerState.getUser().getUsername() +
                        ", Effect: " + effectId + ", Timing: " + effect.getTiming(),
                "WonderBuildManager.applyWonderStageBenefits");
    }
    
    /**
     * Maps wonder name, face, and stage to effect ID.
     * 
     * @param wonderName the name of the wonder
     * @param face the face (A or B)
     * @param stage the stage number (1-based)
     * @return the effect ID, or null if no mapping exists
     */
    private String mapWonderStageToEffectId(String wonderName, String face, int stage) {
        String key = wonderName.toUpperCase() + "_" + face + "_STAGE_" + stage;
        
        return switch (key) {
            // Alexandria A
            case "ALEXANDRIA_A_STAGE_1" -> "ALEXANDRIA_A_STAGE_1_VP_3";
            case "ALEXANDRIA_A_STAGE_2" -> "ALEXANDRIA_A_STAGE_2_MUTABLE_BASE_1";
            case "ALEXANDRIA_A_STAGE_3" -> "ALEXANDRIA_A_STAGE_3_VP_7";
            
            // Alexandria B
            case "ALEXANDRIA_B_STAGE_1" -> "ALEXANDRIA_B_STAGE_1_MUTABLE_BASE_1";
            case "ALEXANDRIA_B_STAGE_2" -> "ALEXANDRIA_B_STAGE_2_MUTABLE_ADVANCED_1";
            case "ALEXANDRIA_B_STAGE_3" -> "ALEXANDRIA_B_STAGE_3_VP_7";
            
            // Babylon A
            case "BABYLON_A_STAGE_1" -> "BABYLON_A_STAGE_1_VP_3";
            case "BABYLON_A_STAGE_2" -> "BABYLON_A_STAGE_2_MUTABLE_SCIENCE_1";
            case "BABYLON_A_STAGE_3" -> "BABYLON_A_STAGE_3_VP_7";
            
            // Babylon B
            case "BABYLON_B_STAGE_1" -> "BABYLON_B_STAGE_1_VP_3";
            case "BABYLON_B_STAGE_2" -> "BABYLON_B_STAGE_2_PLAY_LAST_CARDS";
            case "BABYLON_B_STAGE_3" -> "BABYLON_B_STAGE_3_MUTABLE_SCIENCE_1";
            
            // Ephesos A
            case "EPHESOS_A_STAGE_1" -> "EPHESOS_A_STAGE_1_VP_3";
            case "EPHESOS_A_STAGE_2" -> "EPHESOS_A_STAGE_2_COINS_9";
            case "EPHESOS_A_STAGE_3" -> "EPHESOS_A_STAGE_3_VP_7";
            
            // Ephesos B
            case "EPHESOS_B_STAGE_1" -> "EPHESOS_B_STAGE_1_VP_2_COINS_4_MILITARY_1";
            case "EPHESOS_B_STAGE_2" -> "EPHESOS_B_STAGE_2_VP_3_COINS_4_MILITARY_1";
            case "EPHESOS_B_STAGE_3" -> "EPHESOS_B_STAGE_3_VP_5_COINS_4_MILITARY_1";
            
            // Gizah A
            case "GIZAH_A_STAGE_1" -> "GIZAH_A_STAGE_1_VP_3";
            case "GIZAH_A_STAGE_2" -> "GIZAH_A_STAGE_2_VP_5";
            case "GIZAH_A_STAGE_3" -> "GIZAH_A_STAGE_3_VP_7";
            
            // Gizah B
            case "GIZAH_B_STAGE_1" -> "GIZAH_B_STAGE_1_VP_3";
            case "GIZAH_B_STAGE_2" -> "GIZAH_B_STAGE_2_VP_5";
            case "GIZAH_B_STAGE_3" -> "GIZAH_B_STAGE_3_VP_5";
            case "GIZAH_B_STAGE_4" -> "GIZAH_B_STAGE_4_VP_7";
            
            // Halikarnassos A
            case "HALIKARNASSOS_A_STAGE_1" -> "HALIKARNASSOS_A_STAGE_1_VP_3";
            case "HALIKARNASSOS_A_STAGE_2" -> "HALIKARNASSOS_A_STAGE_2_BUILD_DISCARD";
            case "HALIKARNASSOS_A_STAGE_3" -> "HALIKARNASSOS_A_STAGE_3_VP_7";
            
            // Halikarnassos B
            case "HALIKARNASSOS_B_STAGE_1" -> "HALIKARNASSOS_B_STAGE_1_BUILD_DISCARD";
            case "HALIKARNASSOS_B_STAGE_2" -> "HALIKARNASSOS_B_STAGE_2_BUILD_DISCARD";
            case "HALIKARNASSOS_B_STAGE_3" -> "HALIKARNASSOS_B_STAGE_3_BUILD_DISCARD";
            
            // Olympia A
            case "OLYMPIA_A_STAGE_1" -> "OLYMPIA_A_STAGE_1_VP_3";
            case "OLYMPIA_A_STAGE_2" -> "OLYMPIA_A_STAGE_2_FIRST_CARD_FREE";
            case "OLYMPIA_A_STAGE_3" -> "OLYMPIA_A_STAGE_3_VP_7";
            
            // Olympia B
            case "OLYMPIA_B_STAGE_1" -> "OLYMPIA_B_STAGE_1_BASE_PRICE_1";
            case "OLYMPIA_B_STAGE_2" -> "OLYMPIA_B_STAGE_2_VP_5";
            case "OLYMPIA_B_STAGE_3" -> "OLYMPIA_B_STAGE_3_COPY_VIOLET";
            
            // Rhodes A
            case "RHODES_A_STAGE_1" -> "RHODES_A_STAGE_1_VP_3";
            case "RHODES_A_STAGE_2" -> "RHODES_A_STAGE_2_MILITARY_2";
            case "RHODES_A_STAGE_3" -> "RHODES_A_STAGE_3_VP_7";
            
            // Rhodes B
            case "RHODES_B_STAGE_1" -> "RHODES_B_STAGE_1_VP_3_COINS_3_MILITARY_1";
            case "RHODES_B_STAGE_2" -> "RHODES_B_STAGE_2_VP_4_COINS_4_MILITARY_1";
            
            default -> null;
        };
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
