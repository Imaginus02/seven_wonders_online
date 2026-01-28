package com.reynaud.wonders.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.GameStatus;

public class GameDTO {
    private Long id;

    private List<Long> userIds = new ArrayList<>();
    private List<String> usernames = new ArrayList<>();
    private List<PlayerStateDTO> playerStates = new ArrayList<>();
    private List<Long> ageICardIds = new ArrayList<>();
    private List<Long> ageIICardIds = new ArrayList<>();
    private List<Long> ageIIICardIds = new ArrayList<>();
    private List<Long> discardCardIds = new ArrayList<>();

    @NotNull(message = "Game status is required")
    private GameStatus status = GameStatus.WAITING;

    private Age currentAge;
    private Integer currentTurn = 0;
    private Integer minPlayers = 3;
    private Integer maxPlayers = 7;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long winnerId;
    private String winnerUsername;
    private Integer currentPlayerIndex = 0;

    // Default constructor
    public GameDTO() {
    }

    // Constructor with basic fields
    public GameDTO(Long id, GameStatus status, Integer currentTurn) {
        this.id = id;
        this.status = status;
        this.currentTurn = currentTurn;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<PlayerStateDTO> getPlayerStates() {
        return playerStates;
    }

    public void setPlayerStates(List<PlayerStateDTO> playerStates) {
        this.playerStates = playerStates;
    }

    public List<Long> getAgeICardIds() {
        return ageICardIds;
    }

    public void setAgeICardIds(List<Long> ageICardIds) {
        this.ageICardIds = ageICardIds;
    }

    public List<Long> getAgeIICardIds() {
        return ageIICardIds;
    }

    public void setAgeIICardIds(List<Long> ageIICardIds) {
        this.ageIICardIds = ageIICardIds;
    }

    public List<Long> getAgeIIICardIds() {
        return ageIIICardIds;
    }

    public void setAgeIIICardIds(List<Long> ageIIICardIds) {
        this.ageIIICardIds = ageIIICardIds;
    }

    public List<Long> getDiscardCardIds() {
        return discardCardIds;
    }

    public void setDiscardCardIds(List<Long> discardCardIds) {
        this.discardCardIds = discardCardIds;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Age getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(Age currentAge) {
        this.currentAge = currentAge;
    }

    public Integer getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Integer currentTurn) {
        this.currentTurn = currentTurn;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public Integer getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(Integer currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
}
