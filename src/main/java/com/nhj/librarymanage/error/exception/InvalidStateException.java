package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidStateException extends BusinessException {

    public InvalidStateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
