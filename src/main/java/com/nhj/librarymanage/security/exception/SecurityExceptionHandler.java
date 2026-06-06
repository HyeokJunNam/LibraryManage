package com.nhj.librarymanage.security.exception;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public ProblemDetail handle(SecurityException ex) {
        SecurityError securityError = ex.getSecurityError();

        ProblemDetail pd = ProblemDetail.forStatus(securityError.getStatus());

        pd.setTitle(securityError.getTitle());
        pd.setDetail(securityError.getDetail());
        pd.setProperty("code", securityError.getCode());

        return pd;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handle(AccessDeniedException ex) {
        SecurityError securityError = AuthenticateError.NO_AUTHENTICATION;

        ProblemDetail pd = ProblemDetail.forStatus(securityError.getStatus());

        pd.setTitle(securityError.getTitle());
        pd.setDetail(securityError.getDetail());
        pd.setProperty("code", securityError.getCode());

        return pd;
    }

}
