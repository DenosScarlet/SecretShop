package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.enums.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.workGroup = :workGroup")
    List<User> findUsersByWorkGroup(@Param("workGroup") WorkGroup workGroup);

//    @Modifying
//    @Query("UPDATE User u SET u.balance = u.balance + :cost WHERE u.userId = :userId")
//    void updateBalanceById(@Param("userId") UUID userId, @Param("cost") Integer cost);

    @Modifying
    @Query(
            value = "UPDATE \"user\" SET balance = balance + :cost WHERE user_id = :userId",
            nativeQuery = true
    )
    void updateBalanceById(@Param("userId") UUID userId, @Param("cost") Integer cost);

    @Query("SELECT u.balance FROM User u WHERE u.userId = :userId")
    Integer getBalanceById(@Param("userId") UUID userId);
}
