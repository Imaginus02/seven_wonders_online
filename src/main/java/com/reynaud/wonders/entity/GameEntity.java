package com.reynaud.wonders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games", indexes = {
        @Index(name = "idx_games_status", columnList = "status"),
        @Index(name = "idx_games_created_at", columnList = "created_at")
})
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "game_users",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerStateEntity> playerStates = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "game_age_i_cards",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<CardEntity> ageICards = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "game_age_ii_cards",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<CardEntity> ageIICards = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "game_age_iii_cards",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<CardEntity> ageIIICards = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "game_discard_cards",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<CardEntity> discard = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private GameStatus status = GameStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_age", length = 20)
    private Age currentAge;

    @Column(name = "current_turn", nullable = false)
    private Integer currentTurn = 0;

    @Column(name = "min_players", nullable = false)
    private Integer minPlayers = 3;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers = 7;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private UserEntity winner;

    @Column(name = "current_player_index")
    private Integer currentPlayerIndex = 0;

    // Default constructor
    public GameEntity() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<PlayerStateEntity> getPlayerStates() {
        return playerStates;
    }

    public void setPlayerStates(List<PlayerStateEntity> playerStates) {
        this.playerStates = playerStates;
    }

    public List<CardEntity> getAgeICards() {
        return ageICards;
    }

    public void setAgeICards(List<CardEntity> ageICards) {
        this.ageICards = ageICards;
    }

    public List<CardEntity> getAgeIICards() {
        return ageIICards;
    }

    public void setAgeIICards(List<CardEntity> ageIICards) {
        this.ageIICards = ageIICards;
    }

    public List<CardEntity> getAgeIIICards() {
        return ageIIICards;
    }

    public void setAgeIIICards(List<CardEntity> ageIIICards) {
        this.ageIIICards = ageIIICards;
    }

    public List<CardEntity> getDiscard() {
        return discard;
    }

    public void setDiscard(List<CardEntity> discard) {
        this.discard = discard;
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

    public UserEntity getWinner() {
        return winner;
    }

    public void setWinner(UserEntity winner) {
        this.winner = winner;
    }

    public Integer getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(Integer currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    // Helper methods
    public void addUser(UserEntity user) {
        users.add(user);
    }

    public void removeUser(UserEntity user) {
        users.remove(user);
    }

    public void addPlayerState(PlayerStateEntity playerState) {
        playerStates.add(playerState);
        playerState.setGame(this);
    }

    public void removePlayerState(PlayerStateEntity playerState) {
        playerStates.remove(playerState);
        playerState.setGame(null);
    }

    public boolean isFull() {
        return users.size() >= maxPlayers;
    }

    public boolean hasEnoughPlayers() {
        return users.size() >= minPlayers;
    }
}
