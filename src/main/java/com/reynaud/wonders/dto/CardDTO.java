package com.reynaud.wonders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.EnumMap;
import java.util.Map;

import com.reynaud.wonders.model.Age;
import com.reynaud.wonders.model.CardType;
import com.reynaud.wonders.model.Ressources;

public class CardDTO {
    private Long id;

    @NotBlank(message = "Card name is required")
    private String name;

    @NotNull(message = "Card type is required")
    private CardType type;

    @NotNull(message = "Age is required")
    private Age age;

    private Map<Ressources, Integer> cost = new EnumMap<>(Ressources.class);

    private Integer coinCost = 0;

    private String image;

    @NotNull(message = "Minimum player count is required")
    private Integer minPlayerCount;

    private String incomingLinks;

    private String outgoingLinks;

    // Default constructor
    public CardDTO() {
    }

    // Constructor with all fields
    public CardDTO(Long id, String name, CardType type, Age age, Map<Ressources, Integer> cost, Integer coinCost, Integer minPlayerCount, String incomingLinks, String outgoingLinks, String image) {
        this.id = id;
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

    public Integer getCoinCost() {
        return coinCost;
    }

    public void setCoinCost(Integer coinCost) {
        this.coinCost = coinCost;
    }

    public Integer getMinPlayerCount() {
        return minPlayerCount;
    }

    public void setMinPlayerCount(Integer minPlayerCount) {
        this.minPlayerCount = minPlayerCount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
