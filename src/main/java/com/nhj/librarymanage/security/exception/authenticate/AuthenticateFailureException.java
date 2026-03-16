package com.nhj.librarymanage.security.exception.authenticate;

import com.nhj.librarymanage.security.exception.SecurityException;
import lombok.Getter;

@Getter
public class AuthenticateFailureException extends SecurityException {

    public AuthenticateFailureException(AuthenticateError authenticateError) {
        super(authenticateError);
    }

    public AuthenticateFailureException(AuthenticateError authenticateError, Throwable cause) {
        super(authenticateError, cause);
    }

}
