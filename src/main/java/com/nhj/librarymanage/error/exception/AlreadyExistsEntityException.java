package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyExistsEntityException extends BusinessException {

    public AlreadyExistsEntityException(ErrorCode errorCode) {
        super(errorCode);
    }

}
