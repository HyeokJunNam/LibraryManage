package com.nhj.librarymanage.security.exception.authenticate;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthenticationException extends AuthenticationException {

    private final String code;

    public CustomAuthenticationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public CustomAuthenticationException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}