package com.reynaud.wonders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.reynaud.wonders.model.Ressources;

@Entity
@Table(name = "player_states")
public class PlayerStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @NotNull
    private GameEntity game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private UserEntity user;

    @Column(nullable = false)
    @NotNull
    private Integer position; // Player position at the table (0-6)

    @Column(nullable = false)
    private Integer coins = 3; // Starting coins

    @Column(nullable = false)
    private Integer militaryPoints = 0;

    @Column(nullable = false)
    private Integer victoryPoints = 0;

    @Column(length = 100)
    private String wonderName; // Name of the wonder board

    @Column(name = "wonder_side", length = 1)
    private String wonderSide = "A"; // A or B side

    @Column(nullable = false)
    private Integer wonderStage = 0; // Current wonder stage built (0-3 or 0-4 depending on wonder)

    @ManyToMany
    @JoinTable(
        name = "player_state_cards",
        joinColumns = @JoinColumn(name = "player_state_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<CardEntity> playedCards = new ArrayList<>();

    @Column(length = 500)
    @Convert(converter = RessourceCostConverter.class)
    private Map<Ressources, Integer> resources = new EnumMap<>(Ressources.class);

    // Science symbols count
    @Column(name = "science_tablets")
    private Integer scienceTablets = 0;

    @Column(name = "science_compasses")
    private Integer scienceCompasses = 0;

    @Column(name = "science_gears")
    private Integer scienceGears = 0;

    @Column(name = "science_wildcards")
    private Integer scienceWildcards = 0;

    @Column(name = "has_played_this_turn", nullable = false)
    private Boolean hasPlayedThisTurn = false;

    @ManyToMany
    @JoinTable(
        name = "player_state_hand",
        joinColumns = @JoinColumn(name = "player_state_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<CardEntity> hand = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "player_state_wonder_cards",
        joinColumns = @JoinColumn(name = "player_state_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    @OrderColumn(name = "card_order")
    private List<CardEntity> wonderCards = new ArrayList<>();

    // Default constructor
    public PlayerStateEntity() {
    }

    // Constructor with basic fields
    public PlayerStateEntity(GameEntity game, UserEntity user, Integer position) {
        this.game = game;
        this.user = user;
        this.position = position;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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

    public List<CardEntity> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<CardEntity> playedCards) {
        this.playedCards = playedCards;
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

    public List<CardEntity> getHand() {
        return hand;
    }

    public void setHand(List<CardEntity> hand) {
        this.hand = hand;
    }

    public List<CardEntity> getWonderCards() {
        return wonderCards;
    }

    public void setWonderCards(List<CardEntity> wonderCards) {
        this.wonderCards = wonderCards;
    }
}
