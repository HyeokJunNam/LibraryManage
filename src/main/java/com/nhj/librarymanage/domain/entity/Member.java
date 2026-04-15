package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.security.member.Role;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    private String email;


    public void changeName(String name) {
        this.name = name;
    }
}
