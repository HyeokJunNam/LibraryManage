package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.security.member.Role;
import com.nhj.librarymanage.security.member.SecurityUser;
import com.nhj.librarymanage.security.util.AuthorityUtils;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "member")
public class MemberEntity extends BaseEntity implements SecurityUser {

    @Id
    @Tsid
    private Long id;

    private String loginId;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;


    public void changeName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.generateSimpleGrantedAuthorityList(this.role);
    }

    @NonNull
    @Override
    public String getUsername() {
        return this.loginId;
    }
}
