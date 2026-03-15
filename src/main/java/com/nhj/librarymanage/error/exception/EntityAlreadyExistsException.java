package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.ErrorCode;
import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends BusinessException {

    public EntityAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

}
