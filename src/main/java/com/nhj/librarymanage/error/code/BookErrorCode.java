package com.nhj.librarymanage.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum BookErrorCode implements ErrorCode {

    BOOK_NOT_FOUND("Not found", "요청한 책을 찾을 수 없습니다.", null),
    BOOK_ITEM_NOT_FOUND("Not found", "요청한 책의 보유 정보를 찾을 수 없습니다.", null),
    BOOK_META_DATA_NOT_FOUND("Not found", "해당 ISBN으로 조회된 도서 정보가 없습니다.", null),

    BOOK_NOT_BORROWABLE("Not Borrowable", "대여 가능한 책이 없습니다.", null),
    BOOK_NOT_RETURNABLE("Not Returnable", "대여 되지 않았거나, 이미 반납이 완료된 책입니다.", null),

    BOOK_ITEM_ALREADY_BORROWED("Cannot be changed", "대출 중인 도서가 있어 요청을 처리할 수 없습니다.",null),
    ;

    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String title;
    private final String detail;
    private final String logMessage;

    @Override
    public String getCode() {
        return name();
    }

}