package com.reynaud.wonders.manager;

import com.reynaud.wonders.dao.GameDAO;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.EffectTiming;
import com.reynaud.wonders.model.GameStatus;
import com.reynaud.wonders.service.EffectExecutorService;
import com.reynaud.wonders.service.GameScoringService;
import com.reynaud.wonders.service.LoggingService;
import com.reynaud.wonders.service.PlayerStateService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Manager responsible for managing game state transitions.
 * Handles finishing and canceling games, as well as simple state transitions.
 * 
 * NOTE: Full game startup from scratch (WAITING -> STARTING with initialization)
 * is handled by GameInitManager.startGame(playerIds).
 * This manager handles state transitions for already-initialized games.
 */
@Component
public class GameStateManager {

    private final GameDAO gameDAO;
    private final PlayerStateService playerStateService;
    private final EffectExecutorService effectExecutor;
    private final GameScoringService gameScoringService;
    private final LoggingService loggingService;

    public GameStateManager(GameDAO gameDAO, PlayerStateService playerStateService, EffectExecutorService effectExecutor, GameScoringService gameScoringService, LoggingService loggingService) {
        this.gameDAO = gameDAO;
        this.playerStateService = playerStateService;
        this.effectExecutor = effectExecutor;
        this.gameScoringService = gameScoringService;
        this.loggingService = loggingService;
    }

    /**
     * Transitions an existing game to STARTING state.
     * Used when a game that already has players joins wants to begin play.
     * Player states should already exist.
     * 
     * @param game the game entity to start
     * @return the updated game entity
     * @throws IllegalStateException if the game is not in WAITING status
     */
    @Transactional
    public GameEntity startGame(GameEntity game) {
        loggingService.info("Starting game - GameID: " + game.getId() + ", CurrentStatus: " + game.getStatus(), "GameStateManager.startGame");
        if (game.getStatus() != GameStatus.WAITING) {
            loggingService.error("Cannot start game - Game already started - GameID: " + game.getId() + ", Status: " + game.getStatus(), "GameStateManager.startGame");
            throw new IllegalStateException("Game has already started");
        }

        game.setStatus(GameStatus.STARTING);
        game.setStartedAt(LocalDateTime.now());
        loggingService.info("Game started successfully - GameID: " + game.getId() + ", StartedAt: " + game.getStartedAt(), "GameStateManager.startGame");
        return gameDAO.save(game);
    }

    /**
     * Transitions a game to the FINISHED state with a designated winner.
     * 
     * @param game the game entity to finish
     * @param winner the user entity who won the game
     * @return the updated game entity
     */
    @Transactional
    public GameEntity finishGame(GameEntity game) {
        loggingService.info("Finishing game - GameID: " + game.getId(), "GameStateManager.finishGame");
        game.setStatus(GameStatus.FINISHED);
        game.setFinishedAt(LocalDateTime.now());

        applyPendingEffects(game.getId(), EnumSet.of(EffectTiming.END_OF_GAME));

        game.getPlayerStates().forEach(playerState -> {
            loggingService.info("Calculating points for player - PlayerUsername: " + playerState.getUser().getUsername() + ", CurrentVP: " + playerState.getVictoryPoints(), "GameStateManager.finishGame");         
            
            playerState.setVictoryPoints(playerState.getVictoryPoints() + playerState.getCoins() / 3);
            
            loggingService.info("Coins points added: " + playerState.getCoins() / 3, "GameStateManager.finishGame");

            // Military points are calculated at the end of each age

            // Wonder stage points are added when constructed

            // Blue card points are added when constructed

            // Yellow card points are added when constructed

            int sciencePoints = gameScoringService.calculateSciencePoints(playerState.getScience());
            playerState.setVictoryPoints(playerState.getVictoryPoints() + sciencePoints);
            loggingService.info("Science points added: " + sciencePoints, "GameStateManager.finishGame");
            
        });
        PlayerStateEntity winner = game.getPlayerStates().stream().max((p1, p2) -> p1.getVictoryPoints().compareTo(p2.getVictoryPoints())).orElseThrow();
        game.setWinner(winner.getUser());

        loggingService.info("Game finished successfully - GameID: " + game.getId() + ", FinishedAt: " + game.getFinishedAt(), "GameStateManager.finishGame");
        return gameDAO.save(game);
    }

    /**
     * Transitions a game to the CANCELLED state.
     * 
     * @param game the game entity to cancel
     * @return the updated game entity
     */
    @Transactional
    public GameEntity cancelGame(GameEntity game) {
        loggingService.info("Cancelling game - GameID: " + game.getId() + ", CurrentStatus: " + game.getStatus(), "GameStateManager.cancelGame");
        game.setStatus(GameStatus.CANCELLED);
        game.setFinishedAt(LocalDateTime.now());
        loggingService.info("Game cancelled successfully - GameID: " + game.getId() + ", CancelledAt: " + game.getFinishedAt(), "GameStateManager.cancelGame");
        return gameDAO.save(game);
    }

    @Transactional
    public GameEntity setGameToWaiting(GameEntity game) {
        loggingService.info("Setting game to WAITING state - GameID: " + game.getId() + ", CurrentStatus: " + game.getStatus(), "GameStateManager.setGameToWaiting");
        game.setStatus(GameStatus.WAITING);
        loggingService.info("Game set to WAITING state successfully - GameID: " + game.getId(), "GameStateManager.setGameToWaiting");
        return gameDAO.save(game);
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
