package com.nhj.librarymanage.security.exception.authorize;

import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class CustomAccessDeniedException extends AccessDeniedException {

    private final String code;

    public CustomAccessDeniedException(String code, String message) {
        super(message);
        this.code = code;
    }

    public CustomAccessDeniedException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}