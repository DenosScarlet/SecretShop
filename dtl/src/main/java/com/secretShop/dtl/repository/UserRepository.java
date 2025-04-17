package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.enums.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.workGroup = :workGroup")
    List<User> findUsersByWorkGroup(@Param("workGroup") WorkGroup workGroup);
}
