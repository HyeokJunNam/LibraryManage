package com.nhj.librarymanage.security.exception.authorize;

import com.nhj.librarymanage.security.exception.SecurityError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AuthorizeError implements SecurityError {

    // JWT 인증 실패 목록
    UNEXPECTED_AUTHORIZE("Authorize Error", "예상치 못한 권한 처리 오류가 발생했습니다."),
    AUTHORIZE_FAILURE("Authorize Error", "권한 체크에 실패했습니다."),

    NO_AUTHORIZATION("No Authorize", "사용자 권한 정보가 존재하지 않습니다."),

    EXPIRED_JWT("Authorize Failure", "인증 정보가 만료 되었습니다. 다시 로그인 해주세요."),
    MALFORMED_JWT("Authorize Failure", "인증 정보가 손상 되었습니다."),
    INVALID_SIGN("Authorize Failure", "인증 정보의 서명이 올바르지 않습니다."),


    // Refresh Token
    /*REFRESH_UNABLE_AUTHORIZATION("인증 갱신 정보가 없거나 올바르지 않습니다."),
    REFRESH_EXPIRED_JWT("인증 갱신 정보가 만료 되었습니다. 다시 로그인 해주세요."),
    REFRESH_MALFORMED_JWT("인증 갱신 정보가 손상 되었습니다."),
    REFRESH_INVALID_SIGN("인증 갱신 정보의 서명이 올바르지 않습니다."),
    REFRESH_INVALID_JWT("인증 갱신 정보가 유효하지 않습니다."),*/
    ;


    private final HttpStatus status = HttpStatus.UNAUTHORIZED;
    private final String title;
    private final String detail;

    @Override
    public String getCode() {
        return name();
    }

}