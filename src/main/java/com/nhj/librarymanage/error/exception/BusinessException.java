package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.Error;
import com.nhj.librarymanage.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Error error;

    public BusinessException(Error error) {
        super(error.getDetail());
        this.error = error;
    }
}