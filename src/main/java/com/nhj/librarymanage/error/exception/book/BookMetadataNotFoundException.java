package com.nhj.librarymanage.error.exception.book;

import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.BusinessException;

public class BookMetadataNotFoundException extends BusinessException {

    public BookMetadataNotFoundException(BookErrorCode bookErrorCode) {
        super(bookErrorCode);
    }
}
