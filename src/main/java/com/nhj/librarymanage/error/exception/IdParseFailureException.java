package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.code.CommonErrorCode;

public class IdParseFailureException extends BusinessException {

    public IdParseFailureException(CommonErrorCode commonErrorCode) {
        super(commonErrorCode);
    }

}
