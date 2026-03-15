package com.nhj.librarymanage.security.member;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public abstract class SecurityUserService<T extends SecurityUser> {

    protected abstract Optional<T> findUser(String loginId);

    public Optional<SecurityUser> loadSecurityUser(String loginId) {
        return findUser(loginId).map(user -> user);
    }

}