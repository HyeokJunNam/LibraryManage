package com.nhj.librarymanage.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MailErrorCode implements ErrorCode {

    SEND_FAILURE("Mail Send Failure", "메일 발송에 실패했습니다."),
    PERSONAL_ENCODING_FAILURE("Mail Send Failure", "메일 발신 설정 중 오류가 발생했습니다."),

    EMAIL_CODE_MISMATCH("Verification Failure", "올바르지 않은 인증 코드 입니다."),
    // EMAIL_CODE_EXPIRED("Verification Failure", "이미 만료 된 인증 코드 입니다."),
    EMAIL_CODE_INVALID("Verification Failure", "유효하지 않은 인증 코드 입니다."),
    EMAIL_DUPLICATE("Verification Failure", "이미 가입 된 이메일 입니다."),
    ;

    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String title;
    private final String detail;

    @Override
    public String getCode() {
        return name();
    }

}