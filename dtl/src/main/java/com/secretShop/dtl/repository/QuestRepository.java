package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface QuestRepository extends JpaRepository<Quest, UUID> {
    @Modifying
    @Query("SELECT q.steps_to_complete FROM Quest q WHERE q.quest_id = :questId")
    Integer getStepsToCompleteById(@Param("questId")UUID questId);


}
