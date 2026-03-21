package com.nhj.librarymanage.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MemberErrorCode implements ErrorCode {

    ALREADY_MEMBER("Already Exists", "이미 존재하는 회원 ID 입니다."),
    MEMBER_NOT_FOUND("Not Found", "요청한 회원을 찾을 수 없습니다."),
    ;

    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String title;
    private final String detail;

    @Override
    public String getCode() {
        return name();
    }

}