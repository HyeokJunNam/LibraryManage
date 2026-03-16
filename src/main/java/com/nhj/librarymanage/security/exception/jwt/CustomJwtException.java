package com.nhj.librarymanage.security.exception.jwt;

import com.nhj.librarymanage.security.exception.SecurityError;
import com.nhj.librarymanage.security.exception.SecurityException;

public class CustomJwtException extends SecurityException {

    public CustomJwtException(SecurityError securityError) {
        super(securityError);
    }

    public CustomJwtException(SecurityError securityError, Throwable throwable) {
        super(securityError, throwable);
    }

}