package com.nhj.librarymanage.error.exception;

import com.nhj.librarymanage.error.code.BookErrorCode;
import lombok.Getter;

@Getter
public class TestException extends AjaxException
{
    public TestException(String message) {
        super(BookErrorCode.BOOK_ITEM_NOT_FOUND);
    }

}
