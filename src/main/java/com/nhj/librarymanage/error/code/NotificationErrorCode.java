package com.nhj.librarymanage.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_ALREADY_REQUESTED("Already Requested", "이미 신청한 알림 입니다..", null),
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