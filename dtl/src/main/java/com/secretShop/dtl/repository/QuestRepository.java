package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestRepository extends JpaRepository<Quest, UUID> {
}
