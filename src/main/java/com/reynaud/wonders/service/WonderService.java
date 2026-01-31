package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.WonderDAO;
import com.reynaud.wonders.dto.WonderDTO;
import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.PlayerStateEntity;
import com.reynaud.wonders.entity.WonderEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class WonderService {

    private final WonderDAO wonderDAO;
    private final PlayerStateService playerStateService;

    public WonderService(WonderDAO wonderDAO, PlayerStateService playerStateService) {
        this.wonderDAO = wonderDAO;
        this.playerStateService = playerStateService;
    }

    @Transactional(readOnly = true)
    public List<WonderEntity> getAllWonders() {
        return wonderDAO.findAll();
    }

    @Transactional(readOnly = true)
    public WonderEntity getWonderById(Long id) {
        return wonderDAO.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<WonderEntity> getWondersByName(String name) {
        return wonderDAO.findByName(name);
    }

    @Transactional(readOnly = true)
    public WonderEntity getWonderByNameAndFace(String name, String face) {
        return wonderDAO.findByNameAndFace(name, face);
    }

    @Transactional(readOnly = true)
    public List<WonderEntity> getWondersByFace(String face) {
        return wonderDAO.findByFace(face);
    }

    @Transactional
    public WonderEntity createWonder(WonderEntity wonder) {
        return wonderDAO.save(wonder);
    }

    @Transactional
    public WonderEntity updateWonder(WonderEntity wonder) {
        return wonderDAO.save(wonder);
    }

    @Transactional
    public void deleteWonder(Long id) {
        wonderDAO.deleteById(id);
    }

    // Conversion methods
    public WonderDTO convertToDTO(WonderEntity entity) {
        if (entity == null) {
            return null;
        }

        WonderDTO dto = new WonderDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setFace(entity.getFace());
        dto.setStartingResources(entity.getStartingResources());
        dto.setStageCosts(entity.getStageCosts());
        dto.setNumberOfStages(entity.getNumberOfStages());
        dto.setImage(entity.getImage());

        return dto;
    }

    public List<WonderDTO> convertToDTOList(List<WonderEntity> entities) {
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WonderEntity convertToEntity(WonderDTO dto) {
        if (dto == null) {
            return null;
        }

        WonderEntity entity = new WonderEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setFace(dto.getFace());
        entity.setStartingResources(dto.getStartingResources());
        entity.setStageCosts(dto.getStageCosts());
        entity.setNumberOfStages(dto.getNumberOfStages());
        entity.setImage(dto.getImage());

        return entity;
    }

    /**
     * Handle game creation by assigning random unique wonders to all players
     * Each player gets a unique wonder
     */
    @Transactional
    public void handleGameCreation(GameEntity game) {
        System.out.println("Assigning wonders to players for game ID: " + game.getId());
        // Fetch player states through PlayerStateService following proper service boundaries
        List<PlayerStateEntity> playerStates = playerStateService.getPlayerStatesByGameId(game.getId());
        if (playerStates == null || playerStates.isEmpty()) {
            return;
        }

        List<WonderEntity> allWonders = getAllWonders();
        // Group by name to get pairs (A and B sides)
        Map<String, List<WonderEntity>> wondersByName = allWonders.stream()
                .collect(Collectors.groupingBy(WonderEntity::getName));
        
        List<String> wonderNames = new ArrayList<>(wondersByName.keySet());
        Collections.shuffle(wonderNames);
        Random random = new Random();

        for (int i = 0; i < playerStates.size(); i++) {
            String wonderName = wonderNames.get(i % wonderNames.size());
            List<WonderEntity> wonderFaces = wondersByName.get(wonderName);
            // Randomly select one of the two faces (A or B)
            WonderEntity selectedWonder = wonderFaces.get(random.nextInt(wonderFaces.size()));
            
            playerStates.get(i).setWonder(selectedWonder);
            playerStates.get(i).setWonderStage(0);
            
            // Use PlayerStateService to update following proper service boundaries
            playerStateService.updatePlayerState(playerStates.get(i));
        }
    }
}
