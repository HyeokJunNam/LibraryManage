package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.ErrorCode;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}