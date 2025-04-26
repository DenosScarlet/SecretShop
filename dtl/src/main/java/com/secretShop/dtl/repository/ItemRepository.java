package com.secretShop.dtl.repository;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.enums.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Query("SELECT i FROM Item i WHERE " +
            "(:name IS NULL OR i.itemName = :name) AND " +
            "(:owner IS NULL OR i.owner = :owner) AND " +
            "(:type IS NULL OR i.type = :type)")
    List<Item> searchItems(
            @Param("name") String name,
            @Param("owner") String owner,
            @Param("type") Type type
    );
}