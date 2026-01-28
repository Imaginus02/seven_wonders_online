package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.WonderDAO;
import com.reynaud.wonders.dto.WonderDTO;
import com.reynaud.wonders.entity.WonderEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WonderService {

    private final WonderDAO wonderDAO;

    public WonderService(WonderDAO wonderDAO) {
        this.wonderDAO = wonderDAO;
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
        entity.setNumberOfStages(dto.getNumberOfStages());
        entity.setImage(dto.getImage());

        return entity;
    }
}
