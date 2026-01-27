package com.reynaud.wonders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    private String cost; // JSON string representing costs in terms of resources

    @Column(name = "coin_cost")
    private Integer coinCost = 0; // Coins required in addition to resources

    @Column(length = 1000)
    private String incomingLinks; // JSON array of card names/ids

    @Column(length = 1000)
    private String outgoingLinks; // JSON array of card names/ids

    @Column(length = 100)
    private String effect; // Placeholder for effect function/identifier

    // Default constructor
    public CardEntity() {
    }

    // Constructor with fields
    public CardEntity(String name, CardType type, Age age, String cost, Integer coinCost, String incomingLinks, String outgoingLinks, String effect) {
        this.name = name;
        this.type = type;
        this.age = age;
        this.cost = cost;
        this.coinCost = coinCost;
        this.incomingLinks = incomingLinks;
        this.outgoingLinks = outgoingLinks;
        this.effect = effect;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
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

    public String getEffect() {
        return effect;
    }

    public Integer getCoinCost() {
        return coinCost;
    }

    public void setCoinCost(Integer coinCost) {
        this.coinCost = coinCost;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
