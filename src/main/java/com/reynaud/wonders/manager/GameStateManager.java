package com.reynaud.wonders.manager;

import com.reynaud.wonders.dao.GameDAO;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.model.GameStatus;
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

    public GameStateManager(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
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
        System.out.println("[GameStateManager.startGame] GameId: " + game.getId() + ", Current status: " + game.getStatus());
        if (game.getStatus() != GameStatus.WAITING) {
            System.out.println("[GameStateManager.startGame] ERROR - Game has already started");
            throw new IllegalStateException("Game has already started");
        }

        game.setStatus(GameStatus.STARTING);
        game.setStartedAt(LocalDateTime.now());
        System.out.println("[GameStateManager.startGame] Game started at: " + game.getStartedAt());
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
        System.out.println("[GameStateManager.finishGame] GameId: " + game.getId() + ", Winner: " + (winner != null ? winner.getUsername() : "null"));
        game.setStatus(GameStatus.FINISHED);
        game.setFinishedAt(LocalDateTime.now());
        game.setWinner(winner);
        System.out.println("[GameStateManager.finishGame] Game finished at: " + game.getFinishedAt());
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
        System.out.println("[GameStateManager.cancelGame] GameId: " + game.getId() + ", Current status: " + game.getStatus());
        game.setStatus(GameStatus.CANCELLED);
        game.setFinishedAt(LocalDateTime.now());
        System.out.println("[GameStateManager.cancelGame] Game cancelled at: " + game.getFinishedAt());
        return gameDAO.save(game);
    }
}
