package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.code.CommonErrorCode;
import lombok.Getter;

@Getter
public class InvalidStateException extends BusinessException {

    public InvalidStateException(CommonErrorCode commonErrorCode) {
        super(commonErrorCode);
    }

}
