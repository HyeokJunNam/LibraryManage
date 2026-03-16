package com.nhj.librarymanage.security.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class SecurityException extends AuthenticationException {

    private final SecurityError securityError;

    public SecurityException(SecurityError securityError) {
        super(securityError.getDetail());
        this.securityError = securityError;
    }

    public SecurityException(SecurityError securityError, Throwable cause) {
        super(securityError.getDetail(), cause);
        this.securityError = securityError;
    }

}
