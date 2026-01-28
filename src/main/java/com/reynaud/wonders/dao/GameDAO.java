package com.reynaud.wonders.dao;

import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.entity.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameDAO extends JpaRepository<GameEntity, Long> {

    @Query("SELECT g FROM GameEntity g WHERE g.status = :status ORDER BY g.createdAt DESC")
    List<GameEntity> findByStatus(@Param("status") GameStatus status);

    @Query("SELECT g FROM GameEntity g JOIN g.users u WHERE u.id = :userId ORDER BY g.createdAt DESC")
    List<GameEntity> findByUser(@Param("userId") Long userId);

    @Query("SELECT g FROM GameEntity g WHERE g.status IN :statuses ORDER BY g.createdAt DESC")
    List<GameEntity> findByStatusIn(@Param("statuses") List<GameStatus> statuses);

    @Query("SELECT g FROM GameEntity g WHERE g.status = 'WAITING' AND SIZE(g.users) < g.maxPlayers ORDER BY g.createdAt DESC")
    List<GameEntity> findAvailableGames();

    @Query("SELECT COUNT(g) FROM GameEntity g WHERE g.status IN ('AGE_I', 'AGE_II', 'AGE_III')")
    Long countActiveGames();
}
