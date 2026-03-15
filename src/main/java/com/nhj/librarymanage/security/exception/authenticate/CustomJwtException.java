package com.nhj.librarymanage.security.exception.authenticate;

public class CustomJwtException extends CustomAuthenticationException {

    public CustomJwtException(AuthenticateError authenticateError) {
        super(authenticateError.getCode(), authenticateError.getMessage());
    }

    public CustomJwtException(AuthenticateError authenticateError, Throwable cause) {
        super(authenticateError.getCode(), authenticateError.getMessage(), cause);
    }

    public CustomJwtException(String code, String message) {
        super(code, message);
    }

    public CustomJwtException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

}