package com.reynaud.wonders.dao;

import com.reynaud.wonders.entity.EffectEntity;
import com.reynaud.wonders.model.EffectTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for EffectEntity.
 * Provides database operations for managing game effects.
 */
@Repository
public interface EffectDAO extends JpaRepository<EffectEntity, Long> {

    /**
     * Find an effect by its unique effectId
     * 
     * @param effectId the unique identifier of the effect
     * @return Optional containing the effect if found
     */
    Optional<EffectEntity> findByEffectId(String effectId);

    /**
     * Find all effects with a specific timing
     * 
     * @param timing the EffectTiming to search for
     * @return list of effects matching the timing
     */
    List<EffectEntity> findByTiming(EffectTiming timing);

    /**
     * Find all IMMEDIATE effects (convenience method)
     * 
     * @return list of all immediate effects
     */
    default List<EffectEntity> findAllImmediate() {
        return findByTiming(EffectTiming.IMMEDIATE);
    }

    /**
     * Find all END_OF_TURN effects
     * 
     * @return list of all end-of-turn effects
     */
    default List<EffectEntity> findAllEndOfTurn() {
        return findByTiming(EffectTiming.END_OF_TURN);
    }

    /**
     * Find all DEFERRED effects
     * 
     * @return list of all deferred effects
     */
    default List<EffectEntity> findAllDeferred() {
        return findByTiming(EffectTiming.DEFERRED);
    }

    /**
     * Find all END_OF_GAME effects
     * 
     * @return list of all end-of-game effects
     */
    default List<EffectEntity> findAllEndOfGame() {
        return findByTiming(EffectTiming.END_OF_GAME);
    }
}
