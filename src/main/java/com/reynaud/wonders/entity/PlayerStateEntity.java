package com.reynaud.wonders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.reynaud.wonders.model.Ressources;
import com.reynaud.wonders.model.Science;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wonder_id")
    private WonderEntity wonder; // Reference to the wonder board

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

    @Column(length = 500)
    @Convert(converter = ScienceConverter.class)
    private Map<Science, Integer> science = new EnumMap<>(Science.class);

    @Column(name = "has_played_this_turn", nullable = false)
    private Boolean hasPlayedThisTurn = false;

    @Column(name = "left_base_ressource_price_multiplier", nullable = false)
    private Integer leftBaseRessourcePriceMultiplier = 2;

    @Column(name = "right_base_ressource_price_multiplier", nullable = false)
    private Integer rightBaseRessourcePriceMultiplier = 2;

    @Column(name = "left_advanced_ressource_price_multiplier", nullable = false)
    private Integer leftAdvancedRessourcePriceMultiplier = 2;

    @Column(name = "right_advanced_ressource_price_multiplier", nullable = false)
    private Integer rightAdvancedRessourcePriceMultiplier = 2;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "left_neighbor_id")
    private PlayerStateEntity leftNeighbor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "right_neighbor_id")
    private PlayerStateEntity rightNeighbor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_state_pending_effects",
        joinColumns = @JoinColumn(name = "player_state_id"),
        inverseJoinColumns = @JoinColumn(name = "effect_id")
    )
    private List<EffectEntity> pendingEffects = new ArrayList<>();

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

    public WonderEntity getWonder() {
        return wonder;
    }

    public void setWonder(WonderEntity wonder) {
        this.wonder = wonder;
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

    public PlayerStateEntity getLeftNeighbor() {
        return leftNeighbor;
    }

    public void setLeftNeighbor(PlayerStateEntity leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    public PlayerStateEntity getRightNeighbor() {
        return rightNeighbor;
    }

    public void setRightNeighbor(PlayerStateEntity rightNeighbor) {
        this.rightNeighbor = rightNeighbor;
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

    public List<EffectEntity> getPendingEffects() {
        return pendingEffects;
    }

    public void setPendingEffects(List<EffectEntity> pendingEffects) {
        this.pendingEffects = pendingEffects;
    }

    public void addPendingEffect(EffectEntity effect) {
        if (!this.pendingEffects.contains(effect)) {
            this.pendingEffects.add(effect);
        }
    }

    public void removePendingEffect(EffectEntity effect) {
        this.pendingEffects.remove(effect);
    }

    public boolean hasPendingEffect(String effectId) {
        return this.pendingEffects.stream().anyMatch(e -> e.getEffectId().equals(effectId));
    }
}

