package com.nhj.librarymanage.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode implements Error {

    MEMBER_NOT_FOND("Not Found", "요청한 회원을 찾을 수 없습니다."),
    BOOK_NOT_FOND("Not Found", "요청한 책을 찾을 수 없습니다."),
    ;

    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String title;
    private final String detail;

    @Override
    public String getCode() {
        return name();
    }

}