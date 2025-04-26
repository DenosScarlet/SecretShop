package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuestRepository extends JpaRepository<Quest, UUID> {
    @Query("SELECT q.stepsToComplete FROM Quest q WHERE q.questId = :questId")
    Integer getStepsToCompleteById(@Param("questId") UUID questId);

    @Query("SELECT q.cost FROM Quest q WHERE q.questId = :questId")
    Integer getCostById(@Param("questId") UUID questID);
}
