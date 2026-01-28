package com.reynaud.wonders.controller;

import com.reynaud.wonders.dto.GameDTO;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.GameStatus;
import com.reynaud.wonders.entity.UserEntity;
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
    private final UserService userService;

    public GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
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

    // REST API endpoints
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<GameDTO> createGame(@RequestParam(required = false) Integer minPlayers,
                                              @RequestParam(required = false) Integer maxPlayers) {
        GameEntity game;
        if (minPlayers != null && maxPlayers != null) {
            game = gameService.createGame(minPlayers, maxPlayers);
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
            GameEntity game = gameService.startGame(id);
            return ResponseEntity.ok(gameService.convertToDTO(game));
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
            GameEntity game = gameService.cancelGame(id);
            return ResponseEntity.ok(gameService.convertToDTO(game));
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
