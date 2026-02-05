package com.reynaud.wonders.controller;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.manager.CardPlayManager;
import com.reynaud.wonders.manager.TurnManager;
import com.reynaud.wonders.manager.WonderBuildManager;
import com.reynaud.wonders.model.GameStatus;
import com.reynaud.wonders.service.GameService;
import com.reynaud.wonders.service.LoggingService;
import com.reynaud.wonders.service.PlayerStateService;
import com.reynaud.wonders.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameStateApiController {

    private final GameService gameService;
    private final PlayerStateService playerStateService;
    private final UserService userService;
    private final CardPlayManager cardPlayManager;
    private final WonderBuildManager wonderBuildManager;
    private final TurnManager turnManager;
    private final LoggingService loggingService;

    public GameStateApiController(GameService gameService, PlayerStateService playerStateService,
                                  UserService userService, CardPlayManager cardPlayManager,
                                  WonderBuildManager wonderBuildManager, TurnManager turnManager,
                                  LoggingService loggingService) {
        this.gameService = gameService;
        this.playerStateService = playerStateService;
        this.userService = userService;
        this.cardPlayManager = cardPlayManager;
        this.wonderBuildManager = wonderBuildManager;
        this.turnManager = turnManager;
        this.loggingService = loggingService;
    }

    /**
     * GET /api/hand
     * Returns the cards currently in the player's hand
     */
    @GetMapping("/hand")
    public ResponseEntity<Map<String, List<String>>> getHand(
            @RequestParam Long gameId,
            Authentication authentication) {
        loggingService.debug("API request - Get hand - GameID: " + gameId + ", User: " + (authentication != null ? authentication.getName() : "null"), "GameStateApiController.getHand");
        if (authentication == null) {
            loggingService.warning("Unauthorized hand request - No authentication - GameID: " + gameId, "GameStateApiController.getHand");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> cardImages = playerState.getHand().stream()
                .map(CardEntity::getImage)
                .collect(Collectors.toList());
        
        Map<String, List<String>> response = new HashMap<>();
        response.put("cards", cardImages);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/wonder
     * Returns the player's current wonder
     */
    @GetMapping("/wonder")
    public ResponseEntity<Map<String, String>> getWonder(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        WonderEntity wonder = playerState.getWonder();
        if (wonder == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("wonderImage", wonder.getImage());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/card-backs
     * Returns the card backs for cards used to build the wonder
     */
    @GetMapping("/card-backs")
    public ResponseEntity<Map<String, List<String>>> getCardBacks(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        // Get card backs from wonderCards (ordered list)
        List<String> cardBacks = playerState.getWonderCards().stream()
                .map(card -> card.getAge().toString().toLowerCase() + ".png")
                .collect(Collectors.toList());
        
        Map<String, List<String>> response = new HashMap<>();
        response.put("cardBacks", cardBacks);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/coins
     * Returns the player's current coin count
     */
    @GetMapping("/coins")
    public ResponseEntity<Map<String, Integer>> getCoins(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Integer> response = new HashMap<>();
        response.put("coins", playerState.getCoins());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/played
     * Returns the cards the player has played
     */
    @GetMapping("/played")
    public ResponseEntity<Map<String, List<String>>> getPlayedCards(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> playedCards = playerState.getPlayedCards().stream()
                .map(CardEntity::getImage)
                .collect(Collectors.toList());
        
        Map<String, List<String>> response = new HashMap<>();
        response.put("playedCards", playedCards);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/discarded
     * Returns the cards that have been discarded with their IDs
     */
    @GetMapping("/discarded")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getDiscardedCards(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GameEntity game = gameService.getGameById(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> discardedCards = game.getDiscard().stream()
                .map(card -> {
                    Map<String, Object> cardData = new HashMap<>();
                    cardData.put("id", card.getId());
                    cardData.put("image", card.getImage());
                    return cardData;
                })
                .collect(Collectors.toList());
        
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("discarded", discardedCards);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/players
     * Returns the list of players in the game
     */
    @GetMapping("/players")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getPlayers(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity currentUser = userService.findByUsername(authentication.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PlayerStateEntity> playerStates = playerStateService.getPlayerStatesByGameId(gameId);
        
        List<Map<String, String>> players = new ArrayList<>();
        for (PlayerStateEntity ps : playerStates) {
            Map<String, String> player = new HashMap<>();
            if (ps.getUser().getId().equals(currentUser.getId())) {
                player.put("id", "self");
                player.put("name", "You");
            } else {
                player.put("id", ps.getId().toString());
                player.put("name", ps.getUser().getUsername());
            }
            players.add(player);
        }
        
        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("players", players);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/player/{playerId}
     * Returns the public state of a specific player
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<Map<String, Object>> getPlayerState(
            @PathVariable String playerId,
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity currentUser = userService.findByUsername(authentication.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState;
        
        if ("self".equals(playerId)) {
            playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, currentUser.getId());
        } else {
            try {
                Long playerStateId = Long.parseLong(playerId);
                playerState = playerStateService.getPlayerStateById(playerStateId);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        WonderEntity wonder = playerState.getWonder();
        if (wonder == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> playedCards = playerState.getPlayedCards().stream()
                .map(CardEntity::getImage)
                .collect(Collectors.toList());
        
        // Card backs represent the cards used to build the wonder (ordered)
        List<String> cardBacks = playerState.getWonderCards().stream()
                .map(card -> "back/" + card.getAge().toString().toLowerCase() + ".png")
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("wonderImage", wonder.getImage());
        response.put("coins", playerState.getCoins());
        response.put("playedCards", playedCards);
        response.put("cardBacks", cardBacks);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/card-action
     * Processes a card action (play, build, or discard)
     */
    @PostMapping("/card-action")
    public ResponseEntity<Map<String, Object>> cardAction(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        if (authentication == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String action = (String) request.get("action");
        String cardImage = (String) request.get("card");
        Long gameId = ((Number) request.get("gameId")).longValue();
        
        // Validate action type
        if (!List.of("play", "build", "discard").contains(action)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid action type");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Player state not found");
            return ((BodyBuilder) ResponseEntity.notFound()).body(errorResponse);
        }

        GameEntity game = gameService.getGameById(gameId);
        if (game == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Game not found");
            return ((BodyBuilder) ResponseEntity.notFound()).body(errorResponse);
        }

        // Find the card in the player's hand by image name
        CardEntity cardToPlay = playerState.getHand().stream()
                .filter(card -> cardImage.equals(card.getImage()))
                .findFirst()
                .orElse(null);

        if (cardToPlay == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Card data not sent correctly or card not in hand");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (playerState.getHasPlayedThisTurn()) {
            loggingService.warning("Player already played this turn - GameID: " + gameId + ", User: " + user.getUsername(), "GameStateApiController.cardAction");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Player has already played this turn");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        loggingService.info("Processing card action - Action: " + action + ", Card: " + cardToPlay.getName() + ", Player: " + user.getUsername() + ", GameID: " + gameId, "GameStateApiController.cardAction");

        // Delegate to service for business logic
        boolean actionSuccess = false;
        switch (action) {
            case "play":
                // TODO: Move this check somewhere else
                if (playerState.getPendingEffects().stream().anyMatch(effect -> effect.getEffectId() == "OLYMPIA_A_STAGE_2_FIRST_CARD_FREE")
                &&
                !playerState.getHand().stream().anyMatch(card -> card.getAge() == game.getCurrentAge())) {
                    loggingService.info("Applying OLYMPIA_A_STAGE_2_FIRST_CARD_FREE effect - GameID: " + gameId + ", Player: " + user.getUsername(), "GameStateApiController.cardAction");
                    actionSuccess = cardPlayManager.playCard(playerState, cardToPlay, true);
                } else {
                     actionSuccess = cardPlayManager.playCard(playerState, cardToPlay);
                }
                break;
            case "build":
                actionSuccess = wonderBuildManager.buildWonderWithCard(playerState, cardToPlay);
                break;
            case "discard":
                cardPlayManager.discardCard(playerState, cardToPlay, game.getDiscard());
                gameService.updateGame(game);  // Persist game changes after discard
                actionSuccess = true;
                break;
        }

        if (actionSuccess) {
            loggingService.info("Card action successful - Action: " + action + ", Card: " + cardToPlay.getName() + ", Player: " + user.getUsername() + ", GameID: " + gameId, "GameStateApiController.cardAction");
            playerState.setHasPlayedThisTurn(true);
            turnManager.handleEndOfTurn(game, gameId, playerState);
            playerStateService.updatePlayerState(playerState);
            gameService.updateGame(game);  // Persist game changes after turn handling
        } else {
            loggingService.warning("Card action failed - Action: " + action + ", Card: " + cardToPlay.getName() + ", Player: " + user.getUsername() + ", GameID: " + gameId, "GameStateApiController.cardAction");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", actionSuccess);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/has-played-this-turn
     * Returns whether the player has played a card this turn
     */
    @GetMapping("/has-played-this-turn")
    public ResponseEntity<Map<String, Boolean>> getHasPlayedThisTurn(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasPlayedThisTurn", playerState.getHasPlayedThisTurn());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-actions")
    public ResponseEntity<Map<String, List<String>>> getAvailableActions(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        if (!playerState.getHasPlayedThisTurn()) {
            Map<String, List<String>> response = new HashMap<>();
            response.put("availableActions", List.of("play", "build", "discard"));
            return ResponseEntity.ok(response);
        }

        List<String> actions = new ArrayList<>();
        if (playerState.getPendingEffects().stream()
                .anyMatch(e -> "BUILD_FROM_DISCARD".equals(e.getEffectId()))) {
            actions.add("build_from_discard");
        }

        Map<String, List<String>> response = new HashMap<>();
        response.put("availableActions", actions);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/select-discard-card
     * Handles the BUILD_FROM_DISCARD effect by allowing a player to select a card from the discard pile,
     * remove it, and either play it without cost or use it to build their wonder.
     */
    @PostMapping("/select-discard-card")
    public ResponseEntity<Map<String, String>> selectDiscardCard(
            @RequestParam Long gameId,
            @RequestParam Long cardId,
            @RequestParam String action,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Validate action parameter
        if (!List.of("play", "build").contains(action)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid action. Must be 'play' or 'build'");
            return ResponseEntity.badRequest().body(response);
        }

        GameEntity game = gameService.getGameById(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            return ResponseEntity.notFound().build();
        }

        // Find the card in the discard pile
        CardEntity cardToUse = game.getDiscard().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElse(null);

        if (cardToUse == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Card not found in discard pile");
            return ResponseEntity.badRequest().body(response);
        }

        // Remove card from discard pile
        game.getDiscard().remove(cardToUse);
        loggingService.info("Card removed from discard - GameID: " + gameId + ", CardId: " + cardId + ", Card: " + cardToUse.getName() + ", Action: " + action, "GameStateApiController.selectDiscardCard");

        boolean success = false;
        String actionDescription = "";

        // Perform the requested action
        if ("play".equals(action)) {
            // Play the card without cost (ignoreCost = true)
            success = cardPlayManager.playCard(playerState, cardToUse, true);
            actionDescription = "played";
        } else if ("build".equals(action)) {
            // Build wonder with the card
            success = wonderBuildManager.buildWonderWithCard(playerState, cardToUse);
            actionDescription = "used to build wonder";
        }

        if (!success) {
            // If action failed, put the card back in the discard
            game.getDiscard().add(cardToUse);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to " + action + " the selected card");
            return ResponseEntity.badRequest().body(response);
        }

        // Remove the BUILD_FROM_DISCARD effect from pending effects
        playerState.getPendingEffects().removeIf(e -> "BUILD_FROM_DISCARD".equals(e.getEffectId()));

        // Update game and player state
        playerStateService.updatePlayerState(playerState);
        game.setStatus(GameStatus.PLAYING);
        gameService.updateGame(game);
        
        turnManager.handleEndOfTurn(game, gameId, playerState);

        Map<String, String> response = new HashMap<>();
        response.put("success", "Card " + actionDescription + " successfully from discard");
        response.put("card", cardToUse.getName());
        response.put("action", action);
        loggingService.info("BUILD_FROM_DISCARD effect completed - GameID: " + gameId + ", Player: " + user.getUsername() + ", Card: " + cardToUse.getName() + ", Action: " + action, "GameStateApiController.selectDiscardCard");
        return ResponseEntity.ok(response);
    }
}
