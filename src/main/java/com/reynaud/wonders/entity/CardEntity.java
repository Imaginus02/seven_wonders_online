package com.reynaud.wonders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.EnumMap;
import java.util.Map;

import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.model.Ressources;

@Entity
@Table(name = "cards")
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private CardType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private Age age;

    @Column(length = 500)
    @Convert(converter = RessourceCostConverter.class)
    private Map<Ressources, Integer> cost = new EnumMap<>(Ressources.class);

    @Column(name = "coin_cost")
    private Integer coinCost = 0; // Coins required in addition to resources

    @Column(length = 200)
    private String image;

    @Column(name = "min_player_count", nullable = false)
    @NotNull
    private Integer minPlayerCount; // Minimum number of players for this card to be used

    @Column(length = 1000)
    private String incomingLinks; // JSON array of card names/ids

    @Column(length = 1000)
    private String outgoingLinks; // JSON array of card names/ids

    // Default constructor
    public CardEntity() {
    }

    // Constructor with fields
    public CardEntity(String name, CardType type, Age age, Map<Ressources, Integer> cost, Integer coinCost, Integer minPlayerCount, String incomingLinks, String outgoingLinks, String image) {
        this.name = name;
        this.type = type;
        this.age = age;
        setCost(cost);
        this.coinCost = coinCost;
        this.minPlayerCount = minPlayerCount;
        this.incomingLinks = incomingLinks;
        this.outgoingLinks = outgoingLinks;
        this.image = image;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public Map<Ressources, Integer> getCost() {
        return cost;
    }

    public void setCost(Map<Ressources, Integer> cost) {
        if (cost == null) {
            this.cost = new EnumMap<>(Ressources.class);
        } else {
            this.cost = new EnumMap<>(cost);
        }
    }

    public String getIncomingLinks() {
        return incomingLinks;
    }

    public void setIncomingLinks(String incomingLinks) {
        this.incomingLinks = incomingLinks;
    }

    public String getOutgoingLinks() {
        return outgoingLinks;
    }

    public void setOutgoingLinks(String outgoingLinks) {
        this.outgoingLinks = outgoingLinks;
    }

    public Integer getCoinCost() {
        return coinCost;
    }

    public void setCoinCost(Integer coinCost) {
        this.coinCost = coinCost;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getMinPlayerCount() {
        return minPlayerCount;
    }

    public void setMinPlayerCount(Integer minPlayerCount) {
        this.minPlayerCount = minPlayerCount;
    }
}
