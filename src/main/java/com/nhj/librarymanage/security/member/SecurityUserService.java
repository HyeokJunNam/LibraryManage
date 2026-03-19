package com.nhj.librarymanage.security.member;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.authenticate.AuthenticateFailureException;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public abstract class SecurityUserService<T extends SecurityUser> implements UserDetailsService {

    protected abstract Optional<T> findUser(String loginId);

    public Optional<SecurityUser> loadSecurityUser(String loginId) {
        return findUser(loginId).map(user -> user);
    }

    @NonNull
    @Override
    public SecurityUser loadUserByUsername(@NonNull String username) {
        return findUser(username).orElseThrow(() -> new AuthenticateFailureException(AuthenticateError.MEMBER_NOT_FOUND));
    }
}