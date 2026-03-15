package com.nhj.librarymanage.security.util;

import com.nhj.librarymanage.security.member.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorityUtils {

    public static List<SimpleGrantedAuthority> generateSimpleGrantedAuthorityList(Role... roles) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorityList = new ArrayList<>();

        for (Role role : roles) {
            simpleGrantedAuthorityList.add(new SimpleGrantedAuthority(role.name()));
        }

        return simpleGrantedAuthorityList;
    }

}