package com.nhj.librarymanage.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {

    INVALID_STATE("유효하지 않은 요청", "이미 존재하는 회원 ID 입니다."),

    ;

    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String title;
    private final String detail;

    @Override
    public String getCode() {
        return name();
    }

}