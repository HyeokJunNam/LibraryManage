package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.code.CommonErrorCode;
import com.nhj.librarymanage.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends BusinessException {

    public EntityAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

}
