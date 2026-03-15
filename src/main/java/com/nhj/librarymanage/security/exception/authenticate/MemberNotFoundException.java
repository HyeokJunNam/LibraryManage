package com.nhj.librarymanage.security.exception.authenticate;

public class MemberNotFoundException extends CustomAuthenticationException {

    public MemberNotFoundException(AuthenticateError authenticateError) {
        super(authenticateError.getCode(), authenticateError.getMessage());
    }

    public MemberNotFoundException(String code, String message) {
        super(code, message);
    }

    public MemberNotFoundException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

}