package com.secretShop.dtl.entity;

import com.secretShop.dtl.enums.Status;
import com.secretShop.dtl.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_quests")
public class UsersQuests {
    @Id
    @Column(name = "users_quest_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID users_quest_id;

    @Id
    @Column(name = "user_id")
    private UUID user_id;

    @Id
    @Column(name = "quest_id")
    private UUID quest_id;

    @Column(name = "quest_status")
    @Enumerated(value = EnumType.STRING)
    private Status quest_status;

    @Column(name = "completed_steps")
    private Integer completed_steps;


}
