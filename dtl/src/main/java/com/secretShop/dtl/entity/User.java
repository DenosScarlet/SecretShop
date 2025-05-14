package com.secretShop.dtl.entity;

import com.secretShop.dtl.enums.WorkGroup;
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
@Table(name =  "\"user\"", schema = "public")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "work_group")
    @Enumerated(value = EnumType.STRING)
    private WorkGroup workGroup;

    @Column(name = "balance", nullable = false)
    private Integer balance = 0;


}
