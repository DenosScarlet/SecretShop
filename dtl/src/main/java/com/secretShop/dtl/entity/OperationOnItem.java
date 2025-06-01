package com.secretShop.dtl.entity;

import com.secretShop.dtl.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "operations_on_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationOnItem {
    @Id
    @Column(name = "operations_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID operationsId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    private Item item;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "jsonb", name = "operations_history")
    @JdbcTypeCode(SqlTypes.JSON)
    private String operationHistory;


}
