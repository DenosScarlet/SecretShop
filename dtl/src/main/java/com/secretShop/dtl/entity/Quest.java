package com.secretShop.dtl.entity;

import com.secretShop.dtl.enums.Frequency;
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
@Table(name = "quest")
public class Quest {
    @Id
    @Column(name = "quest_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questId;

    @Column(name = "quest_title")
    private String questTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "steps_to_complete")
    private Integer stepsToComplete;

    @Column(name = "frequency")
    @Enumerated(value = EnumType.STRING)
    private Frequency frequency;

    @Column(name = "work_group")
    @Enumerated(value = EnumType.STRING)
    private WorkGroup workGroup;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "cost")
    private Integer cost;
}
