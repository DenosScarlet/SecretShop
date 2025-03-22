package com.secretShop.dtl.entity;

import com.secretShop.dtl.enums.Frequency;
import com.secretShop.dtl.enums.Type;
import com.secretShop.dtl.enums.WorkGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item")
public class Item {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID item_id;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "description")
    private String description;

    @Column(name = "owner")
    private String owner;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private Type type;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "count")
    private Integer count;
}
