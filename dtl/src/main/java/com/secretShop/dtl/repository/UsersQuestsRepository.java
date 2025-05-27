package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.UsersQuests;
import com.secretShop.dtl.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UsersQuestsRepository extends JpaRepository<UsersQuests, UUID> {
    @Modifying
    @Query(value = "INSERT INTO users_quests (user_id, quest_id, quest_status, completed_steps) " +
            "VALUES (:userId, :questId, 'IN_PROGRESS', 0)",
            nativeQuery = true)
    void createUserQuestRelation(@Param("userId") UUID userId,
                                 @Param("questId") UUID questId);

    @Query("SELECT uq.user.userId FROM UsersQuests uq WHERE uq.quest.questId = :questId")
    List<UUID> findUserIdsByQuestId(@Param("questId") UUID questId);

    @Query("SELECT uq.completedSteps FROM UsersQuests uq WHERE uq.user.userId = :userId")
    Integer getCompletedStepsById(@Param("userId") UUID userId);

    @Query("SELECT uq.questStatus FROM UsersQuests uq WHERE uq.quest.questId = :questId")
    String getQuestStatusById(@Param("questId") UUID questID);

    @Modifying
    @Query("UPDATE UsersQuests uq SET uq.completedSteps = :newStepsValue WHERE uq.user.userId = :userId AND uq.quest.questId = :questId")
    void updateCompletedStepsById(@Param("userId") UUID userId, @Param("questId") UUID questId, @Param("newStepsValue") Integer newStepsValue);

    @Modifying
    @Query("UPDATE UsersQuests uq SET uq.questStatus = :status WHERE uq.user.userId = :userId AND uq.quest.questId = :questId")
    void updateQuestStatusById(@Param("userId") UUID userId, @Param("questId") UUID questId, @Param("status") Status status);

    @Modifying
    @Query("DELETE FROM UsersQuests uq WHERE uq.user.userId = :userId AND uq.quest.questId = :questId")
    void deleteByUserIdAndQuestId(@Param("userId") UUID userId, @Param("questId") UUID questId);


}
