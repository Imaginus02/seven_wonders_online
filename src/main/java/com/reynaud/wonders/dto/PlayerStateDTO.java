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
    private String wonderName;
    private String wonderSide = "A";
    private Integer wonderStage = 0;
    private List<Long> playedCardIds = new ArrayList<>();
    private Map<Ressources, Integer> resources = new EnumMap<>(Ressources.class);
    private Integer scienceTablets = 0;
    private Integer scienceCompasses = 0;
    private Integer scienceGears = 0;
    private Integer scienceWildcards = 0;

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

    public String getWonderName() {
        return wonderName;
    }

    public void setWonderName(String wonderName) {
        this.wonderName = wonderName;
    }

    public String getWonderSide() {
        return wonderSide;
    }

    public void setWonderSide(String wonderSide) {
        this.wonderSide = wonderSide;
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
}
