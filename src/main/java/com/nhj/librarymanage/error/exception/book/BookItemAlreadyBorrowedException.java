package com.nhj.librarymanage.error.exception.book;

import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;

public class BookItemAlreadyBorrowedException extends BusinessException {

    public BookItemAlreadyBorrowedException(BookErrorCode bookErrorCode) {
        super(bookErrorCode);
    }

}
