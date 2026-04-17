package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.security.member.Role;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "member")
public class Member extends BaseEntity {

    @Id
    @Tsid
    private Long id;

    private String loginId;

    private String password;

    @Column(nullable = false, updatable = false)
    private String memberNo;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    private String email;

    private String phoneNumber;




    public void changeName(String name) {
        this.name = name;
    }
}
