package com.nhj.librarymanage.security.exception.authenticate;

public class InvalidLoginRequestException extends CustomAuthenticationException {

    public InvalidLoginRequestException(AuthenticateError authenticateError) {
        super(authenticateError.getCode(), authenticateError.getMessage());
    }

    public InvalidLoginRequestException(AuthenticateError authenticateError, Throwable cause) {
        super(authenticateError.getCode(), authenticateError.getMessage(), cause);
    }

    public InvalidLoginRequestException(String code, String message) {
        super(code, message);
    }

    public InvalidLoginRequestException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

}