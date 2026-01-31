package com.reynaud.wonders.manager;

import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.service.PlayerStateService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manager responsible for managing game turn logic.
 * Handles end-of-turn checks, age transitions, and hand rotations.
 */
@Component
public class TurnManager {

    private final PlayerStateService playerStateService;
    private final CardDistributionManager cardDistributionManager;

    public TurnManager(PlayerStateService playerStateService, CardDistributionManager cardDistributionManager) {
        this.playerStateService = playerStateService;
        this.cardDistributionManager = cardDistributionManager;
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
        System.out.println("[TurnManager.handleEndOfTurn] GameId: " + gameId + ", Player: " + playerState.getUser().getUsername() + ", Remaining cards: " + remainingCards + ", Current age: " + game.getCurrentAge());
        
        if (playerStateService.allPlayersHavePlayedThisTurn(gameId)) {
            System.out.println("[TurnManager.handleEndOfTurn] All players have played this turn");
            // Reset for next turn
            System.out.println("[TurnManager.handleEndOfTurn] Resetting hasPlayedThisTurn for all players");
            for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                ps.setHasPlayedThisTurn(false);
            }
            
            if (remainingCards == 1) {
                // Last card of the round - discard it and move to next age
                System.out.println("[TurnManager.handleEndOfTurn] Last card in hand - discarding and moving to next age");
                for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                    game.getDiscard().add(ps.getHand().remove(0));
                }
                game.setCurrentAge(Age.getNextAge(game.getCurrentAge()));
                System.out.println("[TurnManager.handleEndOfTurn] New age: " + game.getCurrentAge());
                
                if (game.getCurrentAge() == null) {
                    // Game over - all ages completed
                    System.out.println("[TurnManager.handleEndOfTurn] Game over - all ages completed");
                    // TODO: Implement end game scoring and logic
                    return;
                }
                cardDistributionManager.distributeCards(game);
            } else if (remainingCards >= 2) {
                // Advance to next age and rotate hands
                game.setCurrentAge(Age.getNextAge(game.getCurrentAge()));
                boolean clockwise = game.getCurrentAge() == null
                        || game.getCurrentAge() == Age.AGE_I
                        || game.getCurrentAge() == Age.AGE_III;
                System.out.println("[TurnManager.handleEndOfTurn] Rotating hands. Direction: " + (clockwise ? "clockwise" : "counter-clockwise") + ", New age: " + game.getCurrentAge());
                cardDistributionManager.rotateHands(playerStateService.getPlayerStatesByGameId(gameId), clockwise);
            }
            for (PlayerStateEntity ps : playerStateService.getPlayerStatesByGameId(gameId)) {
                playerStateService.updatePlayerState(ps);
            }
        }
    }
}
