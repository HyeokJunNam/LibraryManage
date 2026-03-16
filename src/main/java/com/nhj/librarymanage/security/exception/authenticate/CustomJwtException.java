package com.nhj.librarymanage.security.exception.authenticate;

import com.nhj.librarymanage.security.exception.SecurityException;

public class CustomJwtException extends SecurityException {

    public CustomJwtException(AuthenticateErrorCode authenticateErrorCode) {
        super(authenticateErrorCode);
    }


}