package com.reynaud.wonders.dao;

import com.reynaud.wonders.entity.PlayerStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerStateDAO extends JpaRepository<PlayerStateEntity, Long> {

    @Query("SELECT ps FROM PlayerStateEntity ps WHERE ps.game.id = :gameId ORDER BY ps.position")
    List<PlayerStateEntity> findByGameId(@Param("gameId") Long gameId);

    @Query("SELECT ps FROM PlayerStateEntity ps WHERE ps.user.id = :userId")
    List<PlayerStateEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ps FROM PlayerStateEntity ps WHERE ps.game.id = :gameId AND ps.user.id = :userId")
    PlayerStateEntity findByGameIdAndUserId(@Param("gameId") Long gameId, @Param("userId") Long userId);

    @Query("SELECT ps FROM PlayerStateEntity ps WHERE ps.game.id = :gameId AND ps.position = :position")
    PlayerStateEntity findByGameIdAndPosition(@Param("gameId") Long gameId, @Param("position") Integer position);

    @Query("SELECT COUNT(ps) > 0 FROM PlayerStateEntity ps WHERE ps.game.id = :gameId AND ps.hasPlayedThisTurn = false")
    boolean hasPlayersNotYetPlayed(@Param("gameId") Long gameId);
}
