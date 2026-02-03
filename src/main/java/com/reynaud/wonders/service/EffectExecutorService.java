package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.EffectDAO;
import com.reynaud.wonders.entity.EffectEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.model.Ressources;
import com.reynaud.wonders.model.Science;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for executing game effects.
 * Handles the application of card and wonder stage effects to players.
 * 
 * This service provides a centralized place to apply all effect types,
 * making it easy to add new effect types or modify how effects work.
 * 
 * Parameter format:
 * - VP:n = Victory Points
 * - COINS:n = Coins
 * - MIL:n = Military
 * - index:count = Resources or Science (index based on enum order)
 * - PRICE:side:price = Price multiplier (side: 0=LEFT, 1=RIGHT, 2=BOTH)
 * - keyword = Special effects (VINEYARD, BUILD_FROM_DISCARD, etc.)
 */
@Service
public class EffectExecutorService {

    private final EffectDAO effectDAO;
    private final LoggingService loggingService;

    // Resource enum order: STONE(0), WOOD(1), ORE(2), BRICK(3), GLASS(4), PAPER(5), TEXTILE(6), MUTABLE_BASE(7), MUTABLE_ADVANCED(8)
    private static final Ressources[] RESOURCE_ORDER = {
        Ressources.STONE, Ressources.WOOD, Ressources.ORE, Ressources.BRICK,
        Ressources.GLASS, Ressources.PAPER, Ressources.TEXTILE,
        Ressources.MUTABLE_BASE, Ressources.MUTABLE_ADVANCED
    };

    // Science enum order: TABLET(0), COMPASS(1), GEAR(2), MUTABLE(3)
    private static final Science[] SCIENCE_ORDER = {
        Science.TABLET, Science.COMPASS, Science.GEAR, Science.MUTABLE
    };

    public EffectExecutorService(EffectDAO effectDAO, LoggingService loggingService) {
        this.effectDAO = effectDAO;
        this.loggingService = loggingService;
    }

    /**
     * Apply an effect to a player.
     * This is the main method that routes the effect to the appropriate handler based on its parameters.
     * 
     * @param playerState the player to apply the effect to
     * @param effect the effect to apply
     * @return true if the effect was successfully applied, false otherwise
     */
    @Transactional
    public boolean applyEffect(PlayerStateEntity playerState, EffectEntity effect) {
        if (playerState == null || effect == null) {
            loggingService.warning("Cannot apply effect - PlayerState or Effect is null", "EffectExecutorService.applyEffect");
            return false;
        }

        loggingService.debug("Applying effect - Player: " + playerState.getUser().getUsername() + 
                           ", Effect: " + effect.getEffectId() + 
                           ", Timing: " + effect.getTiming(), "EffectExecutorService.applyEffect");

        String params = effect.getParameters();
        if (params == null || params.isEmpty()) {
            loggingService.warning("Effect has no parameters: " + effect.getEffectId(), "EffectExecutorService.applyEffect");
            return false;
        }

        try {
            // Handle multiple parameters separated by |
            String[] paramParts = params.split("\\|");
            for (String param : paramParts) {
                if (!applySingleParameter(playerState, effect.getEffectId(), param.trim())) {
                    return false;
                }
            }

            loggingService.info("Effect applied successfully - Player: " + playerState.getUser().getUsername() + 
                              ", Effect: " + effect.getEffectId(), "EffectExecutorService.applyEffect");
            return true;

        } catch (Exception e) {
            loggingService.error("Error applying effect - Player: " + playerState.getUser().getUsername() + 
                               ", Effect: " + effect.getEffectId() + 
                               ", Error: " + e.getMessage(), "EffectExecutorService.applyEffect", e);
            return false;
        }
    }

    /**
     * Apply a single parameter from an effect.
     * 
     * @param playerState the player to apply to
     * @param effectId the effect ID for logging
     * @param param the parameter string
     * @return true if applied successfully
     */
    private boolean applySingleParameter(PlayerStateEntity playerState, String effectId, String param) {
        try {
            // Victory Points: VP:n
            if (param.startsWith("VP:")) {
                int vp = Integer.parseInt(param.substring(3));
                playerState.setVictoryPoints(playerState.getVictoryPoints() + vp);
                loggingService.debug("Applied VP - Player: " + playerState.getUser().getUsername() + ", Amount: " + vp, "EffectExecutorService");
                return true;
            }

            // Coins: COINS:n
            if (param.startsWith("COINS:")) {
                int coins = Integer.parseInt(param.substring(6));
                playerState.setCoins(playerState.getCoins() + coins);
                loggingService.debug("Applied COINS - Player: " + playerState.getUser().getUsername() + ", Amount: " + coins, "EffectExecutorService");
                return true;
            }

            // Military: MIL:n
            if (param.startsWith("MIL:")) {
                int mil = Integer.parseInt(param.substring(4));
                playerState.setMilitaryPoints(playerState.getMilitaryPoints() + mil);
                loggingService.debug("Applied MIL - Player: " + playerState.getUser().getUsername() + ", Amount: " + mil, "EffectExecutorService");
                return true;
            }

            // Price multiplier: PRICE:side:price
            if (param.startsWith("PRICE:")) {
                String[] parts = param.substring(6).split(":");
                int side = Integer.parseInt(parts[0]);
                int price = Integer.parseInt(parts[1]);
                applyPriceMultiplier(playerState, side, price);
                return true;
            }

            // Resource/Science with index:count format
            if (param.contains(":") && !param.startsWith("VP:") && !param.startsWith("COINS:") && 
                !param.startsWith("MIL:") && !param.startsWith("PRICE:")) {
                String[] parts = param.split(":");
                
                // Handle mutable pairs like "0_1:1" for STONE_WOOD
                if (parts[0].contains("_")) {
                    // This is a mutable pair - map to appropriate Ressources enum
                    String[] indices = parts[0].split("_");
                    int count = Integer.parseInt(parts[1]);
                    Ressources resource = mapMutablePair(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));
                    if (resource != null) {
                        playerState.getResources().merge(resource, count, Integer::sum);
                        loggingService.debug("Applied resource (mutable) - Player: " + playerState.getUser().getUsername() + 
                                           ", Resource: " + resource + ", Amount: " + count, "EffectExecutorService");
                        return true;
                    }
                } else {
                    int index = Integer.parseInt(parts[0]);
                    int count = Integer.parseInt(parts[1]);

                    // Try as resource first (indices 0-8)
                    if (index >= 0 && index < RESOURCE_ORDER.length) {
                        Ressources resource = RESOURCE_ORDER[index];
                        playerState.getResources().merge(resource, count, Integer::sum);
                        loggingService.debug("Applied resource - Player: " + playerState.getUser().getUsername() + 
                                           ", Resource: " + resource + ", Amount: " + count, "EffectExecutorService");
                        return true;
                    }

                    // Try as science (indices 9-12 using offset of 9)
                    if (index >= 9 && index < 9 + SCIENCE_ORDER.length) {
                        Science science = SCIENCE_ORDER[index - 9];
                        playerState.getScience().merge(science, count, Integer::sum);
                        loggingService.debug("Applied science - Player: " + playerState.getUser().getUsername() + 
                                           ", Science: " + science + ", Amount: " + count, "EffectExecutorService");
                        return true;
                    }
                }
            }

            // Special keywords (END_OF_TURN coin effects, etc.)
            if (applySpecialKeyword(playerState, param)) {
                return true;
            }

            // Unknown keyword
            loggingService.debug("Special effect parameter (unhandled) - Player: " + playerState.getUser().getUsername() + 
                               ", Param: " + param, "EffectExecutorService");
            return true;

        } catch (Exception e) {
            loggingService.warning("Failed to parse parameter: " + param + " for effect: " + effectId + 
                                 ", Error: " + e.getMessage(), "EffectExecutorService.applySingleParameter");
            return false;
        }
    }

    /**
     * Map two resource indices to a mutable pair Ressources enum.
     * 
     * @param index1 first resource index
     * @param index2 second resource index
     * @return the mutable pair Ressources, or null if invalid
     */
    private Ressources mapMutablePair(int index1, int index2) {
        // 0=STONE, 1=WOOD, 2=ORE, 3=BRICK
        if ((index1 == 0 && index2 == 1) || (index1 == 1 && index2 == 0)) return Ressources.STONE_WOOD;
        if ((index1 == 0 && index2 == 2) || (index1 == 2 && index2 == 0)) return Ressources.STONE_ORE;
        if ((index1 == 0 && index2 == 3) || (index1 == 3 && index2 == 0)) return Ressources.STONE_BRICK;
        if ((index1 == 1 && index2 == 2) || (index1 == 2 && index2 == 1)) return Ressources.WOOD_ORE;
        if ((index1 == 1 && index2 == 3) || (index1 == 3 && index2 == 1)) return Ressources.WOOD_BRICK;
        if ((index1 == 2 && index2 == 3) || (index1 == 3 && index2 == 2)) return Ressources.ORE_BRICK;
        return null;
    }

    /**
     * Apply a price multiplier effect to a player.
     * Handles trading post effects that reduce resource purchase costs.
     * 
     * @param playerState the player to apply the effect to
     * @param side 0=LEFT, 1=RIGHT, 2=BOTH
     * @param price the new price
     */
    private void applyPriceMultiplier(PlayerStateEntity playerState, int side, int price) {
        switch (side) {
            case 0: // LEFT
                playerState.setLeftBaseRessourcePriceMultiplier(price);
                loggingService.debug("Applied left base price - Player: " + playerState.getUser().getUsername() + 
                                   ", Price: " + price, "EffectExecutorService");
                break;
            case 1: // RIGHT
                playerState.setRightBaseRessourcePriceMultiplier(price);
                loggingService.debug("Applied right base price - Player: " + playerState.getUser().getUsername() + 
                                   ", Price: " + price, "EffectExecutorService");
                break;
            case 2: // BOTH
                playerState.setLeftAdvancedRessourcePriceMultiplier(price);
                playerState.setRightAdvancedRessourcePriceMultiplier(price);
                loggingService.debug("Applied both advanced prices - Player: " + playerState.getUser().getUsername() + 
                                   ", Price: " + price, "EffectExecutorService");
                break;
        }
    }

    private boolean applySpecialKeyword(PlayerStateEntity playerState, String param) {
        switch (param) {
            case "VINEYARD": {
                int brownCount = countCardsByType(playerState, CardType.BROWN)
                        + countCardsByType(playerState.getLeftNeighbor(), CardType.BROWN)
                        + countCardsByType(playerState.getRightNeighbor(), CardType.BROWN);
                if (brownCount > 0) {
                    playerState.setCoins(playerState.getCoins() + brownCount);
                }
                return true;
            }
            case "LIGHTHOUSE": {
                int yellowCount = countCardsByType(playerState, CardType.YELLOW);
                if (yellowCount > 0) {
                    playerState.setCoins(playerState.getCoins() + yellowCount);
                }
                return true;
            }
            case "HAVEN": {
                int brownCount = countCardsByType(playerState, CardType.BROWN);
                if (brownCount > 0) {
                    playerState.setCoins(playerState.getCoins() + brownCount);
                }
                return true;
            }
            case "CHAMBER_OF_COMMERCE": {
                int yellowCount = countCardsByType(playerState, CardType.YELLOW);
                if (yellowCount > 0) {
                    playerState.setCoins(playerState.getCoins() + (yellowCount * 2));
                }
                return true;
            }
            case "BUILD_FROM_DISCARD":
                // Mark that this player needs to select a card from discard
                // The game will be paused after all end-of-turn effects are applied
                return true;
            default:
                return false;
        }
    }

    private int countCardsByType(PlayerStateEntity playerState, CardType type) {
        if (playerState == null || playerState.getPlayedCards() == null) {
            return 0;
        }
        return (int) playerState.getPlayedCards().stream()
                .filter(card -> card.getType() == type)
                .count();
    }

    /**
     * Check if an effect can be applied (validation).
     * 
     * @param playerState the player to check
     * @param effect the effect to validate
     * @return true if the effect can be applied, false otherwise
     */
    public boolean canApplyEffect(PlayerStateEntity playerState, EffectEntity effect) {
        if (playerState == null || effect == null) {
            return false;
        }

        // Add custom validation logic here if needed
        // For now, all effects can be applied if they have valid parameters
        return effect.getEffectId() != null && !effect.getEffectId().isEmpty();
    }

    /**
     * Remove an effect from pending effects list.
     * Used after a deferred effect has been resolved.
     * 
     * @param playerState the player to remove the effect from
     * @param effectId the ID of the effect to remove
     * @return true if removed, false if not found
     */
    @Transactional
    public boolean removePendingEffect(PlayerStateEntity playerState, String effectId) {
        if (playerState == null || effectId == null) {
            return false;
        }

        EffectEntity effectToRemove = playerState.getPendingEffects().stream()
                .filter(e -> e.getEffectId().equals(effectId))
                .findFirst()
                .orElse(null);

        if (effectToRemove != null) {
            playerState.removePendingEffect(effectToRemove);
            loggingService.debug("Removed pending effect - Player: " + playerState.getUser().getUsername() + 
                               ", Effect: " + effectId, "EffectExecutorService.removePendingEffect");
            return true;
        }

        return false;
    }

    /**
     * Get an effect by its ID from the database.
     * 
     * @param effectId the unique identifier of the effect
     * @return the EffectEntity if found, null otherwise
     */
    public EffectEntity getEffectById(String effectId) {
        return effectDAO.findByEffectId(effectId).orElse(null);
    }
}
