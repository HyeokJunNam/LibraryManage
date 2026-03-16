package com.nhj.librarymanage.security.exception.authenticate;

import com.nhj.librarymanage.security.exception.SecurityException;
import lombok.Getter;

@Getter
public class SecurityAuthenticateException extends SecurityException {

    public SecurityAuthenticateException(AuthenticateErrorCode authenticateErrorCode) {
        super(authenticateErrorCode);
    }

    public SecurityAuthenticateException(AuthenticateErrorCode authenticateErrorCode, Throwable cause) {
        super(authenticateErrorCode, cause);
    }

}
