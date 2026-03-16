package com.nhj.librarymanage.security.exception;

import org.springframework.http.ProblemDetail;
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

}
