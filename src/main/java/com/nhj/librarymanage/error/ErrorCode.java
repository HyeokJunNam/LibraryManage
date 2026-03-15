package com.nhj.librarymanage.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode implements Error {

    ALREADY_MEMBER("Already Exists", "이미 존재하는 회원 ID 입니다."),

    MEMBER_NOT_FOND("Not Found", "요청한 회원을 찾을 수 없습니다."),
    BOOK_NOT_FOND("Not Found", "요청한 책을 찾을 수 없습니다."),

    BORROWED_BOOK("Invalid State", "이미 대여 중인 책 입니다."),
    NOT_BORROWED_BOOK("Invalid State", "대여 중인 책이 아닙니다.")
    ;

    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String title;
    private final String detail;

    @Override
    public String getCode() {
        return name();
    }

}