package com.nhj.librarymanage.security.authenticate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, @NonNull AuthenticationException exception) throws IOException {

        // 로그인 실패 후 처리


        request.getSession().setAttribute(
                "LOGIN_ERROR",
                "아이디 또는 비밀번호가 올바르지 않습니다."
        );

        response.sendRedirect("/login");
    }
}
