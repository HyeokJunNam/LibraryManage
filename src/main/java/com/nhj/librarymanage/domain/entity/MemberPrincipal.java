package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.security.member.AuthenticatedUser;
import com.nhj.librarymanage.security.member.Role;
import com.nhj.librarymanage.security.util.AuthorityUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class MemberPrincipal implements UserDetails, AuthenticatedUser {

    private final Long id;

    private final String loginId;

    private final String password;

    private final String name;

    private final Role role;

    public static MemberPrincipal from(Member member) {
        return MemberPrincipal.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.generateSimpleGrantedAuthorityList(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return name;
    }

}
