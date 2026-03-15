package com.nhj.librarymanage.security.exception.authenticate;

public class LoginFailureException extends CustomAuthenticationException {

    public LoginFailureException(AuthenticateError authenticateError) {
        super(authenticateError.getCode(), authenticateError.getMessage());
    }

    public LoginFailureException(AuthenticateError authenticateError, Throwable cause) {
        super(authenticateError.getCode(), authenticateError.getMessage(), cause);
    }

    public LoginFailureException(String code, String message) {
        super(code, message);
    }

    public LoginFailureException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

}