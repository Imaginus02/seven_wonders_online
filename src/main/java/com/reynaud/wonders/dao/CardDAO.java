package com.reynaud.wonders.dao;

import com.reynaud.wonders.entity.CardEntity;
import com.reynaud.wonders.entity.Age;
import com.reynaud.wonders.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardDAO extends JpaRepository<CardEntity, Long> {

    @Query("SELECT c FROM CardEntity c WHERE c.name = :name")
    CardEntity findByName(@Param("name") String name);

    @Query("SELECT c FROM CardEntity c WHERE c.age = :age")
    List<CardEntity> findByAge(@Param("age") Age age);

    @Query("SELECT c FROM CardEntity c WHERE c.type = :type")
    List<CardEntity> findByType(@Param("type") CardType type);
}
