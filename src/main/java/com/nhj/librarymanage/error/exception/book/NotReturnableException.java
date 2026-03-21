package com.nhj.librarymanage.error.exception.book;

import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class NotReturnableException extends BusinessException {

    public NotReturnableException(BookErrorCode bookErrorCode) {
        super(bookErrorCode);
    }

}
