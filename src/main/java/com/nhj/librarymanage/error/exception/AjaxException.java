package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class AjaxException extends RuntimeException {

    private final ErrorCode errorCode;

    public AjaxException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public AjaxException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDetail(), cause);
        this.errorCode = errorCode;
    }

}