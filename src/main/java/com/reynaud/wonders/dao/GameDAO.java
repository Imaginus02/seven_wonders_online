package com.reynaud.wonders.dao;

import com.reynaud.wonders.entity.GameEntity;
import com.reynaud.wonders.model.GameStatus;

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

    @Query("SELECT g FROM GameEntity g WHERE g.status = :waiting AND SIZE(g.users) < g.nbrPlayers ORDER BY g.createdAt DESC")
    List<GameEntity> findAvailableGames(@Param("waiting") GameStatus waiting);

    @Query("SELECT COUNT(g) FROM GameEntity g WHERE g.status IN :activeStatuses")
    Long countActiveGames(@Param("activeStatuses") List<GameStatus> activeStatuses);
}
