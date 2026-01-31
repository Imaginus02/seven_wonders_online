package com.reynaud.wonders.controller;

import com.reynaud.wonders.dto.GameDTO;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.UserEntity;
import com.reynaud.wonders.manager.GameInitManager;
import com.reynaud.wonders.manager.GameStateManager;
import com.reynaud.wonders.model.GameStatus;
import com.reynaud.wonders.service.GameService;
import com.reynaud.wonders.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final GameInitManager gameInitManager;
    private final GameStateManager gameStateManager;
    private final UserService userService;

    public GameController(GameService gameService, GameInitManager gameInitManager,
                          GameStateManager gameStateManager, UserService userService) {
        this.gameService = gameService;
        this.gameInitManager = gameInitManager;
        this.gameStateManager = gameStateManager;
        this.userService = userService;
    }

    // Web pages
    @GetMapping
    public String gamesPage(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            List<GameEntity> availableGames = gameService.getAvailableGames();
            model.addAttribute("availableGames", gameService.convertToDTOList(availableGames));
            
            UserEntity user = userService.findByUsername(authentication.getName());
            List<GameEntity> myGames = gameService.getGamesByUser(user.getId());
            model.addAttribute("myGames", gameService.convertToDTOList(myGames));
        }
        return "games";
    }

    @GetMapping("/{id}")
    public String gamePage(@PathVariable Long id, Authentication authentication, Model model) {
        GameEntity game = gameService.getGameById(id);
        if (game == null) {
            return "redirect:/games";
        }

        model.addAttribute("game", gameService.convertToDTO(game));
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "game";
    }

    @GetMapping("/create")
    public String createGamePage(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        List<UserEntity> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        model.addAttribute("minPlayers", 3);
        model.addAttribute("maxPlayers", 7);
        return "create-game";
    }

    @PostMapping("/create")
    public String submitCreateGame(
            @RequestParam List<Long> playerIds,
            Authentication authentication,
            Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        // Validate player count
        if (playerIds == null || playerIds.size() < 3 || playerIds.size() > 7) {
            model.addAttribute("error", "Please select between 3 and 7 players");
            List<UserEntity> allUsers = userService.getAllUsers();
            model.addAttribute("users", allUsers);
            model.addAttribute("minPlayers", 3);
            model.addAttribute("maxPlayers", 7);
            return "create-game";
        }

        try {
            // Start game with all players and initialize game logic
            GameEntity game = gameInitManager.startGame(playerIds);
            return "redirect:/play?gameId=" + game.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Error creating game: " + e.getMessage());
            List<UserEntity> allUsers = userService.getAllUsers();
            model.addAttribute("users", allUsers);
            model.addAttribute("minPlayers", 3);
            model.addAttribute("maxPlayers", 7);
            return "create-game";
        }
    }

    // REST API endpoints
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<GameDTO> createGame(@RequestParam(required = false) Integer nbrPlayers) {
        GameEntity game;
        if (nbrPlayers != null) {
            game = gameService.createGame(nbrPlayers);
        } else {
            game = gameService.createGame();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.convertToDTO(game));
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameEntity> games = gameService.getAllGames();
        return ResponseEntity.ok(gameService.convertToDTOList(games));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<GameDTO> getGame(@PathVariable Long id) {
        GameEntity game = gameService.getGameById(id);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameService.convertToDTO(game));
    }

    @GetMapping("/api/available")
    @ResponseBody
    public ResponseEntity<List<GameDTO>> getAvailableGames() {
        List<GameEntity> games = gameService.getAvailableGames();
        return ResponseEntity.ok(gameService.convertToDTOList(games));
    }

    @GetMapping("/api/status/{status}")
    @ResponseBody
    public ResponseEntity<List<GameDTO>> getGamesByStatus(@PathVariable GameStatus status) {
        List<GameEntity> games = gameService.getGamesByStatus(status);
        return ResponseEntity.ok(gameService.convertToDTOList(games));
    }

    @PostMapping("/api/{id}/join")
    @ResponseBody
    public ResponseEntity<GameDTO> joinGame(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            GameEntity game = gameService.addUserToGame(id, user);
            return ResponseEntity.ok(gameService.convertToDTO(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/api/{id}/leave")
    @ResponseBody
    public ResponseEntity<GameDTO> leaveGame(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            GameEntity game = gameService.removeUserFromGame(id, user);
            return ResponseEntity.ok(gameService.convertToDTO(game));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/{id}/start")
    @ResponseBody
    public ResponseEntity<GameDTO> startGame(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            GameEntity game = gameService.getGameById(id);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }
            GameEntity startedGame = gameStateManager.startGame(game);
            return ResponseEntity.ok(gameService.convertToDTO(startedGame));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/api/{id}/cancel")
    @ResponseBody
    public ResponseEntity<GameDTO> cancelGame(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            GameEntity game = gameService.getGameById(id);
            if (game == null) {
                return ResponseEntity.notFound().build();
            }
            GameEntity cancelledGame = gameStateManager.cancelGame(game);
            return ResponseEntity.ok(gameService.convertToDTO(cancelledGame));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteGame(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            gameService.deleteGame(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/active/count")
    @ResponseBody
    public ResponseEntity<Long> countActiveGames() {
        Long count = gameService.countActiveGames();
        return ResponseEntity.ok(count);
    }
}
