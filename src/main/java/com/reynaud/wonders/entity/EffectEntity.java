package com.reynaud.wonders.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.reynaud.wonders.model.EffectTiming;

/**
 * Entity representing a game effect that can be triggered by a card or wonder stage.
 * Effects are predefined and reusable across multiple cards/wonders.
 * Each effect has:
 * - A unique identifier/name
 * - A timing for when it triggers
 * - Parameters for effect values (coins, victory points, military points, resources, etc.)
 * - A triggerEffect method to apply the effect to a player
 */
@Entity
@Table(name = "effects")
public class EffectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank
    private String effectId; // e.g., "ADD_VICTORY_POINTS_2", "ADD_COINS_3", "ADD_WOOD_1"

    @Column(nullable = false, length = 200)
    @NotBlank
    private String description; // Human-readable description

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private EffectTiming timing; // When the effect triggers

    /**
     * Parameters for the effect, encoded as a string.
     * Format depends on effect type:
     * - Victory Points: "VP:3"
     * - Coins: "COINS:9"
     * - Military: "MIL:2"
     * - Resources: "<resource_index>:<count>" (e.g., "0:1" for WOOD, "4:1" for GLASS)
     * - Science: "<science_index>:<count>" (e.g., "0:1" for TABLET)
     * - Price: "<side_index>:<price>" (0=LEFT, 1=RIGHT, 2=BOTH)
     * - Special: Custom string for complex effects (e.g., "VINEYARD", "BUILD_FROM_DISCARD")
     * 
     * Resource indices (Ressources enum order):
     * 0=STONE, 1=WOOD, 2=ORE, 3=BRICK, 4=GLASS, 5=PAPER, 6=TEXTILE, 7=MUTABLE_BASE, 8=MUTABLE_ADVANCED
     * 
     * Science indices (Science enum order):
     * 0=TABLET, 1=COMPASS, 2=GEAR, 3=MUTABLE
     * 
     * Side indices: 0=LEFT, 1=RIGHT, 2=BOTH
     */
    @Column(name = "parameters", length = 100)
    private String parameters;

    // Default constructor
    public EffectEntity() {
    }

    // Constructor with basic fields
    public EffectEntity(String effectId, String description, EffectTiming timing) {
        this.effectId = effectId;
        this.description = description;
        this.timing = timing;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EffectTiming getTiming() {
        return timing;
    }

    public void setTiming(EffectTiming timing) {
        this.timing = timing;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "EffectEntity{" +
                "id=" + id +
                ", effectId='" + effectId + '\'' +
                ", description='" + description + '\'' +
                ", timing=" + timing +
                ", parameters='" + parameters + '\'' +
                '}';
    }
}
