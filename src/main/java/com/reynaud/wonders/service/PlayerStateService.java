package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.PlayerStateDAO;
import com.reynaud.wonders.dto.PlayerStateDTO;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerStateService {

    private final PlayerStateDAO playerStateDAO;
    private final LoggingService loggingService;

    public PlayerStateService(PlayerStateDAO playerStateDAO, LoggingService loggingService) {
        this.playerStateDAO = playerStateDAO;
        this.loggingService = loggingService;
    }

    @Transactional
    public PlayerStateEntity createPlayerState(GameEntity game, UserEntity user, Integer position) {
        loggingService.info("Creating player state - GameID: " + game.getId() + ", UserID: " + user.getId() + ", Username: " + user.getUsername() + ", Position: " + position, "PlayerStateService.createPlayerState");
        PlayerStateEntity playerState = new PlayerStateEntity(game, user, position);
        PlayerStateEntity savedState = playerStateDAO.save(playerState);
        loggingService.info("Player state created successfully - PlayerStateID: " + savedState.getId() + ", GameID: " + game.getId() + ", Username: " + user.getUsername(), "PlayerStateService.createPlayerState");
        return savedState;
    }

    @Transactional
    public PlayerStateEntity updatePlayerState(PlayerStateEntity playerState) {
        loggingService.debug("Updating player state - PlayerStateID: " + playerState.getId() + ", Username: " + playerState.getUser().getUsername() + ", GameID: " + playerState.getGame().getId(), "PlayerStateService.updatePlayerState");
        return playerStateDAO.save(playerState);
    }

    @Transactional(readOnly = true)
    public PlayerStateEntity getPlayerStateById(Long id) {
        return playerStateDAO.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PlayerStateEntity> getPlayerStatesByGameId(Long gameId) {
        return playerStateDAO.findByGameId(gameId);
    }

    @Transactional(readOnly = true)
    public List<PlayerStateEntity> getPlayerStatesByUserId(Long userId) {
        return playerStateDAO.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public PlayerStateEntity getPlayerStateByGameIdAndUserId(Long gameId, Long userId) {
        return playerStateDAO.findByGameIdAndUserId(gameId, userId);
    }

    @Transactional(readOnly = true)
    public PlayerStateEntity getPlayerStateByGameIdAndPosition(Long gameId, Integer position) {
        return playerStateDAO.findByGameIdAndPosition(gameId, position);
    }

    public boolean allPlayersHavePlayedThisTurn(Long gameId) {
        return playerStateDAO.findByGameId(gameId).stream()
                .allMatch(PlayerStateEntity::getHasPlayedThisTurn);
    }

    @Transactional
    public void deletePlayerState(Long id) {
        playerStateDAO.deleteById(id);
    }

    // Conversion methods
    public PlayerStateDTO convertToDTO(PlayerStateEntity entity) {
        if (entity == null) {
            return null;
        }

        PlayerStateDTO dto = new PlayerStateDTO();
        dto.setId(entity.getId());
        dto.setGameId(entity.getGame().getId());
        dto.setUserId(entity.getUser().getId());
        dto.setUsername(entity.getUser().getUsername());
        dto.setPosition(entity.getPosition());
        dto.setCoins(entity.getCoins());
        dto.setMilitaryPoints(entity.getMilitaryPoints());
        dto.setVictoryPoints(entity.getVictoryPoints());
        dto.setWonderId(entity.getWonder().getId());
        dto.setWonderStage(entity.getWonderStage());
        dto.setPlayedCardIds(entity.getPlayedCards().stream()
                .map(card -> card.getId())
                .collect(Collectors.toList()));
        dto.setResources(entity.getResources());
        dto.setScience(entity.getScience());
        dto.setHasPlayedThisTurn(entity.getHasPlayedThisTurn());
        dto.setLeftBaseRessourcePriceMultiplier(entity.getLeftBaseRessourcePriceMultiplier());
        dto.setRightBaseRessourcePriceMultiplier(entity.getRightBaseRessourcePriceMultiplier());
        dto.setLeftAdvancedRessourcePriceMultiplier(entity.getLeftAdvancedRessourcePriceMultiplier());
        dto.setRightAdvancedRessourcePriceMultiplier(entity.getRightAdvancedRessourcePriceMultiplier());
        dto.setHandCardIds(entity.getHand().stream()
                .map(card -> card.getId())
                .collect(Collectors.toList()));
        dto.setWonderCardIds(entity.getWonderCards().stream()
                .map(card -> card.getId())
                .collect(Collectors.toList()));
        if (entity.getLeftNeighbor() != null) {
            dto.setLeftNeighborId(entity.getLeftNeighbor().getId());
        }
        if (entity.getRightNeighbor() != null) {
            dto.setRightNeighborId(entity.getRightNeighbor().getId());
        }

        return dto;
    }

    public List<PlayerStateDTO> convertToDTOList(List<PlayerStateEntity> entities) {
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}
