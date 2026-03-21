package com.nhj.librarymanage.error.exception.book;

import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class NotBorrowableException extends BusinessException {

    public NotBorrowableException(BookErrorCode bookErrorCode) {
        super(bookErrorCode);
    }

}
