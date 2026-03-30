package com.nhj.librarymanage.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MemberErrorCode implements ErrorCode {

    ALREADY_MEMBER("Already Exists", "이미 존재하는 회원 ID 입니다.", null),
    MEMBER_NOT_FOUND("Not Found", "요청한 회원을 찾을 수 없습니다.", null),

    TOKEN_NOT_FOUND("Token Error", "이메일 인증이 되지 않았거나, 인증 유효 시간이 만료되었습니다.", "Redis 내 일치하는 토큰 없음"),
    INVALID_TOKEN("Token Error", "회원 가입 중 오류가 발생했습니다.", "인증 완료 된 토큰 내 이메일과 페이로드 내 이메일이 다름"),
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