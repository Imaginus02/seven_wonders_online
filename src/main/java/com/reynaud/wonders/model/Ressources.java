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
    MUTABLE_ADVANCED,
    // Mutable pairs - can provide ONE of the two resources
    STONE_WOOD,
    STONE_ORE,
    STONE_BRICK,
    WOOD_ORE,
    WOOD_BRICK,
    ORE_BRICK;

    public boolean isRessource() {
        return switch (this) {
            case MUTABLE_BASE, MUTABLE_ADVANCED, STONE_WOOD, STONE_ORE, STONE_BRICK, WOOD_ORE, WOOD_BRICK, ORE_BRICK -> false;
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

    public boolean isMutablePair() {
        return switch (this) {
            case STONE_WOOD, STONE_ORE, STONE_BRICK, WOOD_ORE, WOOD_BRICK, ORE_BRICK -> true;
            default -> false;
        };
    }

    /**
     * Returns the two base resources that this mutable pair can provide.
     * Returns null if this is not a mutable pair.
     */
    public Ressources[] getPairOptions() {
        return switch (this) {
            case STONE_WOOD -> new Ressources[]{STONE, WOOD};
            case STONE_ORE -> new Ressources[]{STONE, ORE};
            case STONE_BRICK -> new Ressources[]{STONE, BRICK};
            case WOOD_ORE -> new Ressources[]{WOOD, ORE};
            case WOOD_BRICK -> new Ressources[]{WOOD, BRICK};
            case ORE_BRICK -> new Ressources[]{ORE, BRICK};
            default -> null;
        };
    }
}