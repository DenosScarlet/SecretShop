package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.OperationOnItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OperationOnItemRepository extends JpaRepository<OperationOnItem, UUID> {
    List<OperationOnItem> findByUserUserId(UUID userId);
    List<OperationOnItem> findByItemItemId(UUID itemId);
}