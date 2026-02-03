package com.reynaud.wonders.model;

/**
 * Enum representing when a card or wonder effect is triggered during the game.
 * This determines at what point in the game turn/round the effect activates.
 */
public enum EffectTiming {
    /**
     * Effect triggered immediately when the card is played
     * (e.g., resource generation, military points)
     */
    IMMEDIATE,
    
    /**
     * Effect triggered at the end of a turn when all players have played
     * (e.g., effects that need to check game state across all players)
     */
    END_OF_TURN,

    /**
     * Effect triggered at the end of a round before the cards are discarded
     * (e.g., effects that activate just before hand discarding)
     */
    END_OF_AGE_BEFORE_DISCARD,
    
    /**
     * Effect triggered at the end of a round afer the cards are discarded and before hands rotation
     * (e.g., effects that activate just before hand rotation)
     */
    END_OF_AGE_AFTER_DISCARD,
    
    /**
     * Effect triggered at the end of the game during final scoring
     * (e.g., victory point calculations based on card combinations)
     */
    END_OF_GAME,
    
    /**
     * Effect that requires deferred/delayed execution at a specific time
     * (e.g., special effects that need player interaction or blocking)
     */
    DEFERRED
}
