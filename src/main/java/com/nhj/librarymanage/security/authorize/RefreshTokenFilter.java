package com.nhj.librarymanage.security.authorize;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class RefreshTokenFilter extends OncePerRequestFilter {

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.pathPattern(HttpMethod.POST,"/refresh");

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final RequestMatcher requestMatcher;

    public RefreshTokenFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        this.requestMatcher = DEFAULT_ANT_PATH_REQUEST_MATCHER;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException, AccessDeniedException {
        if (!requiresAuthentication(request)) {
            filterChain.doFilter(request, response);
        }
        else {
            try {
                log.info("refresh");

            /*Cookie[] cookies = request.getCookies();
            String refreshToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase("refresh_token")) {
                        refreshToken = cookie.getValue();
                    }
                }
            }*/

                // securityService.renewalUserAuthenticate(refreshToken, response);
                // securityService.responseBuilder(response, SecurityError.REFRESH, HttpStatus.OK.value());
                //

            }
            catch (JwtException e) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response, null);
            }
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return this.requestMatcher.matches(request);
    }

}
