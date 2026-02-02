package com.reynaud.wonders.manager;

import com.reynaud.wonders.dao.GameDAO;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.model.GameStatus;
import com.reynaud.wonders.service.LoggingService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    private final LoggingService loggingService;

    public GameStateManager(GameDAO gameDAO, LoggingService loggingService) {
        this.gameDAO = gameDAO;
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
    public GameEntity finishGame(GameEntity game, UserEntity winner) {
        loggingService.info("Finishing game - GameID: " + game.getId() + ", Winner: " + (winner != null ? winner.getUsername() + " (ID: " + winner.getId() + ")" : "No winner"), "GameStateManager.finishGame");
        game.setStatus(GameStatus.FINISHED);
        game.setFinishedAt(LocalDateTime.now());
        game.setWinner(winner);
        loggingService.info("Game finished successfully - GameID: " + game.getId() + ", FinishedAt: " + game.getFinishedAt() + ", Winner: " + (winner != null ? winner.getUsername() : "None"), "GameStateManager.finishGame");
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
}
