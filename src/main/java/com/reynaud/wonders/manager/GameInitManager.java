package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.GameStatus;
import com.reynaud.wonders.service.GameService;
import com.reynaud.wonders.service.PlayerStateService;
import com.reynaud.wonders.service.UserService;
import com.reynaud.wonders.service.WonderService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Manager responsible for initializing and setting up new games.
 * Handles complete game setup including player registration, neighbor assignment, 
 * wonder assignment, and initial card distribution.
 */
@Component
public class GameInitManager {

    private final GameService gameService;
    private final PlayerStateService playerStateService;
    private final WonderService wonderService;
    private final CardDistributionManager cardDistributionManager;
    private final UserService userService;

    public GameInitManager(GameService gameService, PlayerStateService playerStateService,
                           WonderService wonderService, CardDistributionManager cardDistributionManager,
                           UserService userService) {
        this.gameService = gameService;
        this.playerStateService = playerStateService;
        this.wonderService = wonderService;
        this.cardDistributionManager = cardDistributionManager;
        this.userService = userService;
    }

    /**
     * Initialize and start a new game with the given player IDs.
     * This is the complete game startup process that includes:
     * - Creating the game entity
     * - Adding players and creating player states
     * - Setting up neighbor relationships
     * - Assigning wonders
     * - Distributing initial cards
     * - Transitioning game to STARTING state
     * 
     * @param playerIds list of user IDs to add to the game
     * @return the fully initialized and started game entity
     */
    @Transactional
    public GameEntity startGame(List<Long> playerIds) {
        // Create the game with number of players
        GameEntity game = gameService.createGame(playerIds.size());
        game.setCurrentAge(Age.AGE_I);

        // Add players to game and create their initial state
        Integer position = 0;
        for (Long userId : playerIds) {
            UserEntity user = userService.findByIdIfExists(userId);
            if (user != null) {
                // Add user to game
                game = gameService.addUserToGame(game.getId(), user);
                
                // Create PlayerStateEntity for this player
                playerStateService.createPlayerState(game, user, position);
                position++;
            }
        }
        
        // Reload game to get updated player states
        game = gameService.getGameById(game.getId());
        
        // Setup left and right neighbors for each player based on position
        List<PlayerStateEntity> playerStates = playerStateService.getPlayerStatesByGameId(game.getId());
        setupNeighborRelationships(game, playerStates);
        
        // Assign wonders to all players
        wonderService.handleGameCreation(game);
        
        // Initialize card distribution
        cardDistributionManager.distributeCards(game);
        
        // Transition game to STARTING state with timestamp
        game.setStatus(GameStatus.STARTING);
        game.setStartedAt(LocalDateTime.now());
        game = gameService.updateGame(game);
        
        return game;
    }

    /**
     * Setup neighbor relationships for all players based on their positions.
     * Creates circular neighbor links (each player has left and right neighbor).
     * 
     * @param game the game entity
     * @param playerStates list of player states in the game
     */
    private void setupNeighborRelationships(GameEntity game, List<PlayerStateEntity> playerStates) {
        for (PlayerStateEntity playerState : playerStates) {
            int numPlayers = playerStates.size();
            int currentPosition = playerState.getPosition();
            
            // Left neighbor is the player at position-1 (circular)
            int leftPosition = (currentPosition - 1 + numPlayers) % numPlayers;
            PlayerStateEntity leftNeighbor = playerStateService.getPlayerStateByGameIdAndPosition(game.getId(), leftPosition);
            playerState.setLeftNeighbor(leftNeighbor);
            
            // Right neighbor is the player at position+1 (circular)
            int rightPosition = (currentPosition + 1) % numPlayers;
            PlayerStateEntity rightNeighbor = playerStateService.getPlayerStateByGameIdAndPosition(game.getId(), rightPosition);
            playerState.setRightNeighbor(rightNeighbor);
            
            // Update the player state
            playerStateService.updatePlayerState(playerState);
        }
    }
}
