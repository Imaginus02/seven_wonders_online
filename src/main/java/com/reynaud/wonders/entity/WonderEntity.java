package com.reynaud.wonders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.EnumMap;
import java.util.Map;

@Entity
@Table(name = "wonders")
public class WonderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String name;

    @Column(nullable = false, length = 1)
    @NotBlank
    private String face; // "A" or "B"

    @Column(name = "starting_resources", length = 500)
    @Convert(converter = RessourceCostConverter.class)
    private Map<Ressources, Integer> startingResources = new EnumMap<>(Ressources.class);

    @Column(name = "number_of_stages", nullable = false)
    @NotNull
    private Integer numberOfStages;

    @Column(length = 200)
    private String image;

    // Default constructor
    public WonderEntity() {
    }

    // Constructor with basic fields
    public WonderEntity(String name, String face, Integer numberOfStages) {
        this.name = name;
        this.face = face;
        this.numberOfStages = numberOfStages;
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

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public Map<Ressources, Integer> getStartingResources() {
        return startingResources;
    }

    public void setStartingResources(Map<Ressources, Integer> startingResources) {
        this.startingResources = startingResources;
    }

    public Integer getNumberOfStages() {
        return numberOfStages;
    }

    public void setNumberOfStages(Integer numberOfStages) {
        this.numberOfStages = numberOfStages;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
