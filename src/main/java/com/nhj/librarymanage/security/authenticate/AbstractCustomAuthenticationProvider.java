package com.nhj.librarymanage.security.authenticate;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.authenticate.InvalidLoginRequestException;
import com.nhj.librarymanage.security.exception.authenticate.MemberNotFoundException;
import com.nhj.librarymanage.security.exception.authenticate.PasswordMismatchException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AbstractCustomAuthenticationProvider<T extends SecurityUser> implements AuthenticationProvider {

    private final SecurityUserService<T> securityUserService;

    private final PasswordEncoder passwordEncoder;

    private SecurityUser getMemberDetails(String requestLoginId) {
        if (StringUtils.hasText(requestLoginId)) {
            Optional<SecurityUser> optionalSecurityUser = securityUserService.loadSecurityUser(requestLoginId);

            if (optionalSecurityUser.isPresent()) {
                return optionalSecurityUser.get();
            }
            else {
                UsernamePasswordAuthenticationToken authenticationToken = unauthenticated(requestLoginId);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                throw new MemberNotFoundException(AuthenticateError.MEMBER_NOT_FOUND);
            }
        }
        else {
            throw new InvalidLoginRequestException(AuthenticateError.INVALID_LOGIN_REQUEST);
        }
    }

    private void memberPasswordAuthentication(SecurityUser securityUser, String requestPassword) {
        if (!passwordEncoder.matches(requestPassword, securityUser.getPassword())) {
            UsernamePasswordAuthenticationToken authenticationToken = unauthenticated(securityUser.getLoginId());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            throw new PasswordMismatchException(AuthenticateError.PASSWORD_MISMATCH);
        }
    }


    private UsernamePasswordAuthenticationToken authenticated(SecurityUser securityUser) {
        List<SimpleGrantedAuthority> authorityList = AuthorityUtils.generateSimpleGrantedAuthorityList(securityUser.getRole());

        UsernamePasswordAuthenticationToken authenticatedToken = UsernamePasswordAuthenticationToken.authenticated(securityUser.getLoginId(), null, authorityList);
        authenticatedToken.setDetails(securityUser);

        return authenticatedToken;
    }

    private UsernamePasswordAuthenticationToken unauthenticated(String loginId) {
        return UsernamePasswordAuthenticationToken.unauthenticated(loginId, null);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String requestLoginId = (String) authentication.getPrincipal();
        SecurityUser securityUser = getMemberDetails(requestLoginId);

        String requestPassword = (String) authentication.getCredentials();

        memberPasswordAuthentication(securityUser, requestPassword);

        return authenticated(securityUser);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}