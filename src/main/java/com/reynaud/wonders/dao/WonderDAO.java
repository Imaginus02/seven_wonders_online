package com.reynaud.wonders.dao;

import com.reynaud.wonders.entity.WonderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WonderDAO extends JpaRepository<WonderEntity, Long> {

    @Query("SELECT w FROM WonderEntity w WHERE w.name = :name")
    List<WonderEntity> findByName(@Param("name") String name);

    @Query("SELECT w FROM WonderEntity w WHERE w.name = :name AND w.face = :face")
    WonderEntity findByNameAndFace(@Param("name") String name, @Param("face") String face);

    @Query("SELECT w FROM WonderEntity w WHERE w.face = :face")
    List<WonderEntity> findByFace(@Param("face") String face);
}
