package com.nhj.librarymanage.security.authenticate;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.authenticate.AuthenticateFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
//@Component
public class AbstractCustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private UserDetails getUser(String requestLoginId) {
        try {
            if (StringUtils.hasText(requestLoginId)) {
                return userDetailsService.loadUserByUsername(requestLoginId); // TODO 구현체 없는 상태임?

            }
            else {
                throw new AuthenticateFailureException(AuthenticateError.INVALID_LOGIN_REQUEST);
            }
        }
        catch (UsernameNotFoundException ex) {
            throw new AuthenticateFailureException(AuthenticateError.MEMBER_NOT_FOUND, ex);
        }

    }

    private void validatePassword(UserDetails userDetails, String requestPassword) {
        if (!passwordEncoder.matches(requestPassword, userDetails.getPassword())) {
            throw new AuthenticateFailureException(AuthenticateError.LOGIN_FAILURE);
        }
    }

    private UsernamePasswordAuthenticationToken authenticated(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorityList = userDetails.getAuthorities();

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(userDetails.getUsername(), null, authorityList);
        authenticationToken.setDetails(userDetails);

        return authenticationToken;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String requestLoginId = (String) authentication.getPrincipal();
        String requestPassword = (String) authentication.getCredentials();

        UserDetails userDetails = getUser(requestLoginId);
        validatePassword(userDetails, requestPassword);

        return authenticated(userDetails);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}