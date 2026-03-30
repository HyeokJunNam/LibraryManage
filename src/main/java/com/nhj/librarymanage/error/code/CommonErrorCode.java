package com.nhj.librarymanage.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {

    INVALID_STATE("유효하지 않은 요청", "시스템 오류가 발생했습니다.", null),

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