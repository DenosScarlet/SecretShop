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
    private UUID quest_id;

    @Column(name = "quest_title")
    private String quest_title;

    @Column(name = "description")
    private String description;

    @Column(name = "frequency")
    @Enumerated(value = EnumType.STRING)
    private Frequency frequency;

    @Column(name = "work_group")
    @Enumerated(value = EnumType.STRING)
    private WorkGroup work_group;

    @Column(name = "start_date")
    private LocalDateTime start_date;

    @Column(name = "end_date")
    private LocalDateTime end_date;

    @Column(name = "cost")
    private Integer cost;
}
