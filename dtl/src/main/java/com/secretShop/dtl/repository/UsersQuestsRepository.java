package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.UsersQuests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UsersQuestsRepository extends JpaRepository<UsersQuests, UUID> {
    @Modifying
    @Query(value = "INSERT INTO users_quests (user_id, quest_id, quest_status, completed_steps) " +
            "VALUES (:userId, :questId, 'IN_PROGRESS', 0)",
            nativeQuery = true)
    void createUserQuestRelation(@Param("userId") UUID userId,
                                 @Param("questId") UUID questId);
}
