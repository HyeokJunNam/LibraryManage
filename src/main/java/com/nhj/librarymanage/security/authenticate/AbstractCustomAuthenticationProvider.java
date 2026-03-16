package com.nhj.librarymanage.security.authenticate;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.authenticate.AuthenticateFailureException;
import com.nhj.librarymanage.security.member.SecurityUser;
import com.nhj.librarymanage.security.member.SecurityUserService;
import com.nhj.librarymanage.security.util.AuthorityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AbstractCustomAuthenticationProvider<T extends SecurityUser> implements AuthenticationProvider {

    private final SecurityUserService<T> securityUserService;

    private final PasswordEncoder passwordEncoder;

    private SecurityUser getUser(String requestLoginId) {
        if (StringUtils.hasText(requestLoginId)) {
            return securityUserService.loadSecurityUser(requestLoginId)
                    .orElseThrow(() -> new AuthenticateFailureException(AuthenticateError.MEMBER_NOT_FOUND));
        }
        else {
            throw new AuthenticateFailureException(AuthenticateError.INVALID_LOGIN_REQUEST);
        }
    }

    private void validatePassword(SecurityUser securityUser, String requestPassword) {
        if (!passwordEncoder.matches(requestPassword, securityUser.getPassword())) {
            throw new AuthenticateFailureException(AuthenticateError.LOGIN_FAILURE);
        }
    }

    private UsernamePasswordAuthenticationToken authenticated(SecurityUser securityUser) {
        List<SimpleGrantedAuthority> authorityList = AuthorityUtils.generateSimpleGrantedAuthorityList(securityUser.getRole());

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(securityUser.getLoginId(), null, authorityList);
        authenticationToken.setDetails(securityUser);

        return authenticationToken;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String requestLoginId = (String) authentication.getPrincipal();
        String requestPassword = (String) authentication.getCredentials();

        SecurityUser securityUser = getUser(requestLoginId);
        validatePassword(securityUser, requestPassword);

        return authenticated(securityUser);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}