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
public class OperationOnItem {
    @Id
    @Column(name = "operations_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID operationsId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    private Item itemId;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "jsonb", name = "operation_history")
    private String operationsHistory;
}
