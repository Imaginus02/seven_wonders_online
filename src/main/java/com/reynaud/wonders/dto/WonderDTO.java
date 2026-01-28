package com.reynaud.wonders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.EnumMap;
import java.util.Map;

import com.reynaud.wonders.model.Ressources;

public class WonderDTO {
    private Long id;

    @NotBlank(message = "Wonder name is required")
    private String name;

    @NotBlank(message = "Face is required")
    private String face;

    private Map<Ressources, Integer> startingResources = new EnumMap<>(Ressources.class);

    @NotNull(message = "Number of stages is required")
    private Integer numberOfStages;

    private String image;

    // Default constructor
    public WonderDTO() {
    }

    // Constructor with all fields
    public WonderDTO(Long id, String name, String face, Map<Ressources, Integer> startingResources, Integer numberOfStages, String image) {
        this.id = id;
        this.name = name;
        this.face = face;
        this.startingResources = startingResources;
        this.numberOfStages = numberOfStages;
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
