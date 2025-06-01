package com.secretShop.dtl.entity;

import com.secretShop.dtl.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "users_quests")
public class UsersQuests {
    @Id
    @Column(name = "users_quest_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID usersQuestId;

    //    @Id
    //    @Column(name = "user_id")
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    //    @Id
    //    @Column(name = "quest_id")
    @ManyToOne
    @JoinColumn(name = "quest_id", referencedColumnName = "quest_id")
    private Quest quest;

    @Column(name = "quest_status")
    @Enumerated(value = EnumType.STRING)
    private Status questStatus = Status.IN_PROGRESS;

    @Column(name = "completed_steps")
    private Integer completedSteps = 0;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UsersQuests that = (UsersQuests) o;
        return getUsersQuestId() != null && Objects.equals(getUsersQuestId(), that.getUsersQuestId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
