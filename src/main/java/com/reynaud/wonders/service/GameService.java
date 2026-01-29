package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.GameDAO;
import com.reynaud.wonders.dto.GameDTO;
import com.reynaud.wonders.entity.*;
import com.reynaud.wonders.model.GameStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameDAO gameDAO;
    private final PlayerStateService playerStateService;

    public GameService(GameDAO gameDAO, PlayerStateService playerStateService) {
        this.gameDAO = gameDAO;
        this.playerStateService = playerStateService;
    }

    @Transactional
    public GameEntity createGame() {
        GameEntity game = new GameEntity();
        game.setStatus(GameStatus.WAITING);
        return gameDAO.save(game);
    }

    @Transactional
    public GameEntity createGame(Integer nbrPlayers) {
        GameEntity game = new GameEntity();
        game.setStatus(GameStatus.WAITING);
        game.setNbrPlayers(nbrPlayers);
        return gameDAO.save(game);
    }

    @Transactional
    public GameEntity updateGame(GameEntity game) {
        return gameDAO.save(game);
    }

    @Transactional(readOnly = true)
    public GameEntity getGameById(Long id) {
        return gameDAO.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<GameEntity> getAllGames() {
        return gameDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<GameEntity> getGamesByStatus(GameStatus status) {
        return gameDAO.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<GameEntity> getGamesByUser(Long userId) {
        return gameDAO.findByUser(userId);
    }

    @Transactional(readOnly = true)
    public List<GameEntity> getAvailableGames() {
        return gameDAO.findAvailableGames(GameStatus.WAITING);
    }

    @Transactional(readOnly = true)
    public Long countActiveGames() {
        return gameDAO.countActiveGames(List.of(GameStatus.AGE_I, GameStatus.AGE_II, GameStatus.AGE_III));
    }

    @Transactional
    public GameEntity addUserToGame(Long gameId, UserEntity user) {
        GameEntity game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        if (game.getStatus() != GameStatus.WAITING) {
            throw new IllegalStateException("Game has already started");
        }

        game.addUser(user);
        return gameDAO.save(game);
    }

    @Transactional
    public GameEntity removeUserFromGame(Long gameId, UserEntity user) {
        GameEntity game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        game.removeUser(user);
        return gameDAO.save(game);
    }

    @Transactional
    public GameEntity startGame(Long gameId) {
        GameEntity game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        if (game.getStatus() != GameStatus.WAITING) {
            throw new IllegalStateException("Game has already started");
        }

        game.setStatus(GameStatus.STARTING);
        game.setStartedAt(LocalDateTime.now());
        
        // Initialize player states
        int position = 0;
        for (UserEntity user : game.getUsers()) {
            playerStateService.createPlayerState(game, user, position++);
        }

        return gameDAO.save(game);
    }

    @Transactional
    public GameEntity finishGame(Long gameId, UserEntity winner) {
        GameEntity game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        game.setStatus(GameStatus.FINISHED);
        game.setFinishedAt(LocalDateTime.now());
        game.setWinner(winner);

        return gameDAO.save(game);
    }

    @Transactional
    public GameEntity cancelGame(Long gameId) {
        GameEntity game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        game.setStatus(GameStatus.CANCELLED);
        game.setFinishedAt(LocalDateTime.now());

        return gameDAO.save(game);
    }

    @Transactional
    public void deleteGame(Long id) {
        gameDAO.deleteById(id);
    }

    // Conversion methods
    public GameDTO convertToDTO(GameEntity entity) {
        if (entity == null) {
            return null;
        }

        GameDTO dto = new GameDTO();
        dto.setId(entity.getId());
        dto.setUserIds(entity.getUsers().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList()));
        dto.setUsernames(entity.getUsers().stream()
                .map(UserEntity::getUsername)
                .collect(Collectors.toList()));
        dto.setPlayerStates(playerStateService.convertToDTOList(entity.getPlayerStates()));
        dto.setAgeICardIds(entity.getAgeICards().stream()
                .map(CardEntity::getId)
                .collect(Collectors.toList()));
        dto.setAgeIICardIds(entity.getAgeIICards().stream()
                .map(CardEntity::getId)
                .collect(Collectors.toList()));
        dto.setAgeIIICardIds(entity.getAgeIIICards().stream()
                .map(CardEntity::getId)
                .collect(Collectors.toList()));
        dto.setDiscardCardIds(entity.getDiscard().stream()
                .map(CardEntity::getId)
                .collect(Collectors.toList()));
        dto.setStatus(entity.getStatus());
        dto.setCurrentAge(entity.getCurrentAge());
        dto.setCurrentTurn(entity.getCurrentTurn());
        dto.setNbrPlayers(entity.getNbrPlayers());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setStartedAt(entity.getStartedAt());
        dto.setFinishedAt(entity.getFinishedAt());
        
        if (entity.getWinner() != null) {
            dto.setWinnerId(entity.getWinner().getId());
            dto.setWinnerUsername(entity.getWinner().getUsername());
        }
        
        dto.setCurrentPlayerIndex(entity.getCurrentPlayerIndex());

        return dto;
    }

    public List<GameDTO> convertToDTOList(List<GameEntity> entities) {
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
