package com.reynaud.wonders.model;

public enum Ressources {
    STONE,
    WOOD,
    ORE,
    BRICK,
    GLASS,
    PAPER,
    TEXTILE,
    MUTABLE_BASE,
    MUTABLE_ADVANCED;

    public boolean isRessource() {
        return switch (this) {
            case MUTABLE_BASE,MUTABLE_ADVANCED -> false;
            default -> true;
        };
    }

    public boolean isBaseRessource() {
        return switch (this) {
            case STONE, WOOD, ORE, BRICK -> true;
            default -> false;
        };
    }

    public boolean isAdvancedRessource() {
        return switch (this) {
            case GLASS, PAPER, TEXTILE -> true;
            default -> false;
        };
    }
}