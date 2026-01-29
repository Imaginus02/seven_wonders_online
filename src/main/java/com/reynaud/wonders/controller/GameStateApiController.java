package com.reynaud.wonders.controller;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.service.CardService;
import com.reynaud.wonders.service.GameService;
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
    private final CardService cardService;
    private final PlayerStateService playerStateService;
    private final UserService userService;

    public GameStateApiController(GameService gameService, CardService cardService,
                                  PlayerStateService playerStateService, UserService userService) {
        this.gameService = gameService;
        this.cardService = cardService;
        this.playerStateService = playerStateService;
        this.userService = userService;
    }

    /**
     * GET /api/hand
     * Returns the cards currently in the player's hand
     */
    @GetMapping("/hand")
    public ResponseEntity<Map<String, List<String>>> getHand(
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

        String wonderImage = generateWonderImageName(playerState.getWonderName(), playerState.getWonderSide());
        
        Map<String, String> response = new HashMap<>();
        response.put("wonderImage", wonderImage);
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
     * Returns the cards that have been discarded
     */
    @GetMapping("/discarded")
    public ResponseEntity<Map<String, List<String>>> getDiscardedCards(
            @RequestParam Long gameId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GameEntity game = gameService.getGameById(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> discardedCards = game.getDiscard().stream()
                .map(CardEntity::getImage)
                .collect(Collectors.toList());
        
        Map<String, List<String>> response = new HashMap<>();
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

        String wonderImage = generateWonderImageName(playerState.getWonderName(), playerState.getWonderSide());
        List<String> playedCards = playerState.getPlayedCards().stream()
                .map(CardEntity::getImage)
                .collect(Collectors.toList());
        
        // Card backs represent the cards used to build the wonder (ordered)
        List<String> cardBacks = playerState.getWonderCards().stream()
                .map(card -> "back/" + card.getAge().toString().toLowerCase() + ".png")
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("wonderImage", wonderImage);
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
    public ResponseEntity<Map<String, Boolean>> cardAction(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        if (authentication == null) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String action = (String) request.get("action");
        String cardImage = (String) request.get("card");
        Long gameId = ((Number) request.get("gameId")).longValue();
        
        // Validate action type
        if (!List.of("play", "build", "discard").contains(action)) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        PlayerStateEntity playerState = playerStateService.getPlayerStateByGameIdAndUserId(gameId, user.getId());
        if (playerState == null) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return ((BodyBuilder) ResponseEntity.notFound()).body(errorResponse);
        }

        // Find the card in the player's hand by image name
        CardEntity cardToPlay = playerState.getHand().stream()
                .filter(card -> cardImage.equals(card.getImage()))
                .findFirst()
                .orElse(null);

        if (cardToPlay == null) {
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // TODO: Implement actual game logic for each action type
        // For now, just remove the card from hand
        switch (action) {
            case "play":
                // Add to played cards
                playerState.getHand().remove(cardToPlay);
                playerState.getPlayedCards().add(cardToPlay);
                break;
            case "build":
                // Build wonder stage with this card (card goes under wonder)
                playerState.getHand().remove(cardToPlay);
                playerState.setWonderStage(playerState.getWonderStage() + 1);
                // TODO: Apply wonder stage benefits
                break;
            case "discard":
                // Add to discard pile and gain 3 coins
                GameEntity game = gameService.getGameById(gameId);
                playerState.getHand().remove(cardToPlay);
                game.getDiscard().add(cardToPlay);
                playerState.setCoins(playerState.getCoins() + 3);
                gameService.updateGame(game);
                break;
        }

        playerStateService.updatePlayerState(playerState);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to generate wonder image name from wonder name and side
     */
    private String generateWonderImageName(String wonderName, String wonderSide) {
        if (wonderName == null || wonderName.isEmpty()) {
            return "gizahA.png"; // default
        }
        // Convert wonder name to lowercase and add side
        String cleanName = wonderName.toLowerCase().replaceAll("\\s+", "");
        return cleanName + (wonderSide != null ? wonderSide : "A") + ".png";
    }
}
