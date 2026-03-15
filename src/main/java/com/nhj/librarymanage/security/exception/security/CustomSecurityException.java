package com.nhj.librarymanage.security.exception.security;

import lombok.Getter;

@Getter
public class CustomSecurityException extends RuntimeException {

    public CustomSecurityException(String message) {
        super(message);
    }

    public CustomSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

}