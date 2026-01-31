package com.reynaud.wonders.dto;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.reynaud.wonders.model.Ressources;

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
    private Integer scienceTablets = 0;
    private Integer scienceCompasses = 0;
    private Integer scienceGears = 0;
    private Integer scienceWildcards = 0;
    private Boolean hasPlayedThisTurn = false;
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

    public Integer getScienceTablets() {
        return scienceTablets;
    }

    public void setScienceTablets(Integer scienceTablets) {
        this.scienceTablets = scienceTablets;
    }

    public Integer getScienceCompasses() {
        return scienceCompasses;
    }

    public void setScienceCompasses(Integer scienceCompasses) {
        this.scienceCompasses = scienceCompasses;
    }

    public Integer getScienceGears() {
        return scienceGears;
    }

    public void setScienceGears(Integer scienceGears) {
        this.scienceGears = scienceGears;
    }

    public Integer getScienceWildcards() {
        return scienceWildcards;
    }

    public void setScienceWildcards(Integer scienceWildcards) {
        this.scienceWildcards = scienceWildcards;
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
}
