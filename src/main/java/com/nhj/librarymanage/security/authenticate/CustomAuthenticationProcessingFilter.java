package com.nhj.librarymanage.security.authenticate;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateErrorCode;
import com.nhj.librarymanage.security.exception.authenticate.SecurityAuthenticateException;
import com.nhj.librarymanage.security.jwt.JwtProvider;
import com.nhj.librarymanage.security.member.SecurityUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtProvider jwtProvider;

    // 이 필터를 걸리게 할 Request 를 설정한다.
    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/login");

    public CustomAuthenticationProcessingFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint, JwtProvider jwtProvider) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, @NonNull HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        UsernamePasswordAuthenticationToken authenticationToken;

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(loginRequestDto.loginId(), loginRequestDto.password());

        } catch (IOException e) {
            authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(null, null);
        }

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain, Authentication authResult) {
        SecurityUser securityUser = (SecurityUser) authResult.getDetails();

        if (securityUser == null) {
            throw new SecurityAuthenticateException(AuthenticateErrorCode.AUTHENTICATION_FAILURE);
        }

        String accessToken = jwtProvider.generateToken(securityUser);
        String refreshToken = jwtProvider.generateRefreshToken(securityUser.getId().toString());

        response.addHeader(HttpHeaders.AUTHORIZATION, accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken);

        //postAuth.loginSuccess(memberDetails, refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException failed) throws AuthenticationException, ServletException, IOException {
        SecurityContextHolder.clearContext();

        // postAuth.loginFailure(authentication);

        authenticationEntryPoint.commence(request, response, failed);
    }

}