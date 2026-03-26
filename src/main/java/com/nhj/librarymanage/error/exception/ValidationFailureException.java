package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.code.ErrorCode;

public class ValidationFailureException extends BusinessException {

    public ValidationFailureException(ErrorCode errorCode) {
        super(errorCode);
    }

}
