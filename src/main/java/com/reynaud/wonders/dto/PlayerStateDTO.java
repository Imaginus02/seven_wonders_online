package com.reynaud.wonders.dto;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.reynaud.wonders.model.Ressources;
import com.reynaud.wonders.model.Science;

public class PlayerStateDTO {
    private Long id;

    @NotNull(message = "Game ID is required")
    private Long gameId;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String username;

    @NotNull(message = "Position is required")
    private Integer position;

    private Integer coins = 3;
    private Integer militaryPoints = 0;
    private Integer victoryPoints = 0;
    private Integer wonderStage = 0;
    private List<Long> playedCardIds = new ArrayList<>();
    private Map<Ressources, Integer> resources = new EnumMap<>(Ressources.class);
    private Map<Science, Integer> science = new EnumMap<>(Science.class);
    private Boolean hasPlayedThisTurn = false;
    private Integer leftBaseRessourcePriceMultiplier = 2;
    private Integer rightBaseRessourcePriceMultiplier = 2;
    private Integer leftAdvancedRessourcePriceMultiplier = 2;
    private Integer rightAdvancedRessourcePriceMultiplier = 2;
    private List<Long> handCardIds = new ArrayList<>();
    private List<Long> wonderCardIds = new ArrayList<>();
    private Long wonderId;
    private Long leftNeighborId;
    private Long rightNeighborId;

    // Default constructor
    public PlayerStateDTO() {
    }

    // Constructor with basic fields
    public PlayerStateDTO(Long id, Long gameId, Long userId, String username, Integer position) {
        this.id = id;
        this.gameId = gameId;
        this.userId = userId;
        this.username = username;
        this.position = position;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Integer getMilitaryPoints() {
        return militaryPoints;
    }

    public void setMilitaryPoints(Integer militaryPoints) {
        this.militaryPoints = militaryPoints;
    }

    public Integer getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(Integer victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public Long getWonderId() {
        return wonderId;
    }

    public void setWonderId(Long wonderId) {
        this.wonderId = wonderId;
    }

    public Integer getWonderStage() {
        return wonderStage;
    }

    public void setWonderStage(Integer wonderStage) {
        this.wonderStage = wonderStage;
    }

    public List<Long> getPlayedCardIds() {
        return playedCardIds;
    }

    public void setPlayedCardIds(List<Long> playedCardIds) {
        this.playedCardIds = playedCardIds;
    }

    public Map<Ressources, Integer> getResources() {
        return resources;
    }

    public void setResources(Map<Ressources, Integer> resources) {
        this.resources = resources;
    }

    public Map<Science, Integer> getScience() {
        return science;
    }

    public void setScience(Map<Science, Integer> science) {
        this.science = science;
    }

    public Boolean getHasPlayedThisTurn() {
        return hasPlayedThisTurn;
    }

    public void setHasPlayedThisTurn(Boolean hasPlayedThisTurn) {
        this.hasPlayedThisTurn = hasPlayedThisTurn;
    }

    public List<Long> getHandCardIds() {
        return handCardIds;
    }

    public void setHandCardIds(List<Long> handCardIds) {
        this.handCardIds = handCardIds;
    }

    public List<Long> getWonderCardIds() {
        return wonderCardIds;
    }

    public void setWonderCardIds(List<Long> wonderCardIds) {
        this.wonderCardIds = wonderCardIds;
    }

    public Long getLeftNeighborId() {
        return leftNeighborId;
    }

    public void setLeftNeighborId(Long leftNeighborId) {
        this.leftNeighborId = leftNeighborId;
    }

    public Long getRightNeighborId() {
        return rightNeighborId;
    }

    public void setRightNeighborId(Long rightNeighborId) {
        this.rightNeighborId = rightNeighborId;
    }

    public Integer getLeftBaseRessourcePriceMultiplier() {
        return leftBaseRessourcePriceMultiplier;
    }

    public void setLeftBaseRessourcePriceMultiplier(Integer leftBaseRessourcePriceMultiplier) {
        this.leftBaseRessourcePriceMultiplier = leftBaseRessourcePriceMultiplier;
    }

    public Integer getRightBaseRessourcePriceMultiplier() {
        return rightBaseRessourcePriceMultiplier;
    }

    public void setRightBaseRessourcePriceMultiplier(Integer rightBaseRessourcePriceMultiplier) {
        this.rightBaseRessourcePriceMultiplier = rightBaseRessourcePriceMultiplier;
    }

    public Integer getLeftAdvancedRessourcePriceMultiplier() {
        return leftAdvancedRessourcePriceMultiplier;
    }

    public void setLeftAdvancedRessourcePriceMultiplier(Integer leftAdvancedRessourcePriceMultiplier) {
        this.leftAdvancedRessourcePriceMultiplier = leftAdvancedRessourcePriceMultiplier;
    }

    public Integer getRightAdvancedRessourcePriceMultiplier() {
        return rightAdvancedRessourcePriceMultiplier;
    }

    public void setRightAdvancedRessourcePriceMultiplier(Integer rightAdvancedRessourcePriceMultiplier) {
        this.rightAdvancedRessourcePriceMultiplier = rightAdvancedRessourcePriceMultiplier;
    }
}
