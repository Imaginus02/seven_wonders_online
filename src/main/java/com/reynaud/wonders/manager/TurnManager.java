package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.EffectTiming;
import com.reynaud.wonders.model.GameStatus;
import com.reynaud.wonders.service.EffectExecutorService;
import com.reynaud.wonders.service.LoggingService;
import com.reynaud.wonders.service.PlayerStateService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Manager responsible for managing game turn logic.
 * Handles end-of-turn checks, age transitions, and hand rotations.
 */
@Component
public class TurnManager {

    private final PlayerStateService playerStateService;
    private final CardDistributionManager cardDistributionManager;
    private final LoggingService loggingService;
    private final EffectExecutorService effectExecutor;
    private final GameStateManager gameStateManager;

    public TurnManager(PlayerStateService playerStateService, CardDistributionManager cardDistributionManager,
                       LoggingService loggingService, EffectExecutorService effectExecutor,
                       GameStateManager gameStateManager) {
        this.playerStateService = playerStateService;
        this.cardDistributionManager = cardDistributionManager;
        this.loggingService = loggingService;
        this.effectExecutor = effectExecutor;
        this.gameStateManager = gameStateManager;
    }

    /**
     * Handles end of turn logic by checking if all players have completed their turns.
     * If all players have played, resets the turn flags and either discards the last remaining card
     * and distributes new hands (end of round), or advances to the next age and rotates hands (end of age).
     * Note: Caller is responsible for persisting the game entity changes.
     * 
     * @param game the game entity
     * @param gameId the unique identifier of the game
     * @param playerState the player state entity of the current player
     */
    @Transactional
    public void handleEndOfTurn(GameEntity game, Long gameId, PlayerStateEntity playerState) {
        int remainingCards = playerState.getHand().size();
        loggingService.debug("End of turn check - GameID: " + gameId + ", Player: " + playerState.getUser().getUsername() + ", RemainingCards: " + remainingCards + ", CurrentAge: " + game.getCurrentAge(), "TurnManager.handleEndOfTurn");
        
        if (playerStateService.allPlayersHavePlayedThisTurn(gameId)) {
            loggingService.info("All players completed turn - GameID: " + gameId + ", CurrentAge: " + game.getCurrentAge(), "TurnManager.handleEndOfTurn");
            // Reset for next turn
            loggingService.debug("Resetting turn flags for all players - GameID: " + gameId, "TurnManager.handleEndOfTurn");
            for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                ps.setHasPlayedThisTurn(false);
            }
            
            applyPendingEffects(gameId, EnumSet.of(EffectTiming.IMMEDIATE, EffectTiming.END_OF_TURN));

            // Check if any player has a pending BUILD_FROM_DISCARD effect
            // If so, pause the game to wait for API call to select a card from discard
            if (hasPendingBuildFromDiscard(game, gameId)) {
                gameStateManager.setGameToWaiting(game);
                loggingService.info("Game paused for BUILD_FROM_DISCARD effect - GameID: " + gameId, "TurnManager.handleEndOfTurn");
            }

            if (game.getStatus() == GameStatus.WAITING) {
                loggingService.info("Game is in WAITING status - skipping end of turn processing - GameID: " + gameId, "TurnManager.handleEndOfTurn");
                return;
            }

            if (remainingCards <= 1) {

                // Check if any player can play the last card with BABYLON_B_STAGE_2_PLAY_LAST_CARDS effect
                List<PlayerStateEntity> players = playerStateService.getPlayerStatesByGameId(gameId);
                boolean hasPlayersWithCards = players.stream().anyMatch(ps -> !ps.getHand().isEmpty());
                boolean hasBabylonEffect = players.stream()
                        .anyMatch(ps -> ps.getPendingEffects() != null && ps.getPendingEffects().stream()
                                .anyMatch(effect -> "BABYLON_B_STAGE_2_PLAY_LAST_CARDS".equals(effect.getEffectId())));
                
                if (hasPlayersWithCards && hasBabylonEffect) {
                    players.stream()
                            .filter(ps -> ps.getPendingEffects() != null && ps.getPendingEffects().stream()
                                    .anyMatch(effect -> "BABYLON_B_STAGE_2_PLAY_LAST_CARDS".equals(effect.getEffectId())))
                            .findFirst()
                            .ifPresent(ps -> ps.setHasPlayedThisTurn(false));
                    return;
                }

                // Last card of the round - discard it and move to next age
                loggingService.info("Last card in hand - discarding and moving to next age - GameID: " + gameId + ", CurrentAge: " + game.getCurrentAge(), "TurnManager.handleEndOfTurn");
                
                applyPendingEffects(gameId, EnumSet.of(EffectTiming.END_OF_AGE_BEFORE_DISCARD));

                for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                    game.getDiscard().add(ps.getHand().remove(0));
                }

                applyPendingEffects(gameId, EnumSet.of(EffectTiming.END_OF_AGE_AFTER_DISCARD));

                

                game.setCurrentAge(Age.getNextAge(game.getCurrentAge()));
                loggingService.info("Age advanced - GameID: " + gameId + ", NewAge: " + game.getCurrentAge(), "TurnManager.handleEndOfTurn");
                
                if (game.getCurrentAge() == null) {
                    applyPendingEffects(gameId, EnumSet.of(EffectTiming.END_OF_GAME));
                    loggingService.info("Game complete - All ages finished - GameID: " + gameId, "TurnManager.handleEndOfTurn");
                    // TODO: Implement end game scoring and logic
                    return;
                }
                cardDistributionManager.distributeCards(game);
            } else if (remainingCards >= 2) {
                boolean clockwise = game.getCurrentAge() == null
                        || game.getCurrentAge() == Age.AGE_I
                        || game.getCurrentAge() == Age.AGE_III;
                loggingService.info("Rotating hands - GameID: " + gameId + ", Direction: " + (clockwise ? "clockwise" : "counter-clockwise") + ", NewAge: " + game.getCurrentAge(), "TurnManager.handleEndOfTurn");
                cardDistributionManager.rotateHands(playerStateService.getPlayerStatesByGameId(gameId), clockwise);
            }
            for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                playerStateService.updatePlayerState(ps);
            }


        }
    }

    /**
     * Check if any player has a pending BUILD_FROM_DISCARD effect that needs resolution.
     * 
     * @param game the game entity
     * @param gameId the game ID
     * @return true if BUILD_FROM_DISCARD is pending for any player, false otherwise
     */
    private boolean hasPendingBuildFromDiscard(GameEntity game, Long gameId) {
        List<PlayerStateEntity> players = playerStateService.getPlayerStatesByGameId(gameId);
        for (PlayerStateEntity ps : players) {
            if (ps.getPendingEffects() != null) {
                boolean hasBuildFromDiscard = ps.getPendingEffects().stream()
                        .anyMatch(e -> "BUILD_FROM_DISCARD".equals(e.getEffectId()));
                if (hasBuildFromDiscard) {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyPendingEffects(Long gameId, EnumSet<EffectTiming> timings) {
        List<PlayerStateEntity> players = playerStateService.getPlayerStatesByGameId(gameId);

        for (PlayerStateEntity ps : players) {
            if (ps.getPendingEffects() == null || ps.getPendingEffects().isEmpty()) {
                continue;
            }

            List<com.reynaud.wonders.entity.EffectEntity> toApply = new ArrayList<>(ps.getPendingEffects().stream()
                    .filter(effect -> effect.getTiming() != null && timings.contains(effect.getTiming()))
                    .toList());

            for (com.reynaud.wonders.entity.EffectEntity effect : toApply) {
                effectExecutor.applyEffect(ps, effect);
                //TODO: Remove this ugly condition an handle it in a more generic way
                if (effect.getEffectId() != "BABYLON_B_STAGE_2_PLAY_LAST_CARDS") {
                    ps.removePendingEffect(effect);
                }
            }
        }
    }
}
