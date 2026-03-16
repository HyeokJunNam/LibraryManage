package com.nhj.librarymanage.security.exception.authorize;

import com.nhj.librarymanage.security.exception.SecurityException;
import lombok.Getter;

@Getter
public class AuthorizeFailureException extends SecurityException {

    public AuthorizeFailureException(AuthorizeError authorizeError) {
        super(authorizeError);
    }

    public AuthorizeFailureException(AuthorizeError authorizeError, Throwable cause) {
        super(authorizeError, cause);
    }

}
