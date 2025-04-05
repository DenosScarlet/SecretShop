package com.secretShop.dtl.entity;

import com.secretShop.dtl.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operations_on_item")
public class OperationonItem {
    @Id
    @Column(name = "operations_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID operations_id;

    @Id
    @Column(name = "user_id")
    private UUID user_id;

    @Id
    @Column(name = "item_id")
    private UUID item_id;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "jsonb", name = "operation_history")
    private String operation_history;
}
