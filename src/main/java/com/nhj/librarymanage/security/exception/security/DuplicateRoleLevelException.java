package com.nhj.librarymanage.security.exception.security;

public class DuplicateRoleLevelException extends CustomSecurityException {

    public DuplicateRoleLevelException(String message) {
        super(message);
    }

    public DuplicateRoleLevelException(String message, Throwable cause) {
        super(message, cause);
    }

}