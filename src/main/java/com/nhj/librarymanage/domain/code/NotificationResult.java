package com.nhj.librarymanage.domain.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NotificationResult {

    NOTIFIED("발송 완료"),
    CANCELED("신청 취소"),
    FAILED("발송 실패")
    ;

    private final String label;

    public String getCode() {
        return name();
    }

}
