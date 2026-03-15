package com.nhj.librarymanage.security.exception.authenticate;

public class PasswordMismatchException extends CustomAuthenticationException {

    public PasswordMismatchException(AuthenticateError authenticateError) {
        super(authenticateError.getCode(), authenticateError.getMessage());
    }

    public PasswordMismatchException(String code, String message) {
        super(code, message);
    }

    public PasswordMismatchException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

}