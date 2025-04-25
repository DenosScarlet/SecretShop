package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.UsersQuests;
import com.secretShop.dtl.enums.Status;
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

    @Query("SELECT uq.completed_steps FROM UsersQuests uq WHERE uq.user.user_id = :userId")
    Integer getCompletedStepsById(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE UsersQuests uq SET uq.completed_steps = :newStepsValue WHERE uq.user.user_id = :userId AND uq.quest.quest_id = :questId")
    void updateCompletedStepsById(@Param("userId") UUID userId, @Param("questId") UUID questId, @Param("newStepsValue") Integer newStepsValue);

    @Modifying
    @Query("UPDATE UsersQuests uq SET uq.quest_status = :status WHERE uq.user.user_id = :userId AND uq.quest.quest_id = :questId")
    void updateQuestStatusById(@Param("userId") UUID userId, @Param("questId") UUID questId, @Param("status") Status status);
}
