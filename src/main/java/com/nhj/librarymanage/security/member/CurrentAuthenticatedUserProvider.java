package com.nhj.librarymanage.security.member;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.authenticate.AuthenticateFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentAuthenticatedUserProvider {

    public Optional<Long> findCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthenticatedUser authenticatedUser)) {
            return Optional.empty();
        }

        Object id = authenticatedUser.getId();

        if (!(id instanceof Long memberId)) {
            return Optional.empty();
        }

        return Optional.of(memberId);
    }

    public Long getCurrentUserId() {
        return findCurrentUserId()
                .orElseThrow(() -> new AuthenticateFailureException(AuthenticateError.MEMBER_NOT_FOUND));
    }

}
