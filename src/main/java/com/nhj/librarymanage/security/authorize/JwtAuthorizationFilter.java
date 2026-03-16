package com.nhj.librarymanage.security.authorize;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.jwt.CustomJwtException;
import com.nhj.librarymanage.security.jwt.JwtProvider;
import com.nhj.librarymanage.security.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtProvider jwtProvider;

    private static final List<RequestMatcher> SKIP_REQUEST_MATCHER = List.of(
            RequestMatherFactory.of(HttpMethod.POST, "/login"),
            RequestMatherFactory.of(HttpMethod.POST, "/refresh"),
            RequestMatherFactory.of(HttpMethod.POST, "/logout"),
            RequestMatherFactory.of(HttpMethod.POST, "/test")
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException, AccessDeniedException {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

            if (StringUtils.hasText(authorization)) {
                Claims claims = jwtProvider.getClaims(jwtProvider.parseJwt(authorization));
                usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken.authenticated(claims, null, JwtUtils.extractRole(claims));
            }
            else {
                throw new CustomJwtException(AuthenticateError.NO_AUTHENTICATION);
            }
            //securityService.verifyAccountBlack(UUID.fromString(claims.getAudience()), authorization);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);
        }
        catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(request, response, e);
        }
        catch (JwtException e) {
            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(request, response, new CustomJwtException(AuthenticateError.INVALID_AUTHENTICATION, e));
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return SKIP_REQUEST_MATCHER.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }

}