package com.nhj.librarymanage.security.exception.authenticate;

import com.nhj.librarymanage.security.exception.SecurityError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AuthenticateError implements SecurityError {

    UNEXPECTED_AUTHENTICATION("Authenticate Error", "예상치 못한 인증 오류가 발생했습니다."),
    AUTHENTICATION_FAILURE("Authenticate Error", "사용자 인증에 실패했습니다."),

    NO_AUTHENTICATION("No Authenticate", "사용자 인증 정보가 존재하지 않습니다."),
    INVALID_AUTHENTICATION("No Authenticate", "인증 정보가 만료되었거나 유효하지 않습니다. 다시 로그인 해주세요."),

    INVALID_LOGIN_REQUEST("Login Failure", "로그인 요청 형식이 올바르지 않습니다."),
    LOGIN_FAILURE("Login Failure", "사용자 정보가 없거나, ID 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND("Login Failure", "사용자 정보를 찾을 수 없습니다."),


    // Login reject, BlackList, Session
    /*DOUBLE_LOGIN_DETECTED("동일한 아이디로 다른 곳에서 로그인이 확인되어 로그아웃 되었습니다."),
    SESSION_BLACK_JWT("사용자 작업에 의해 인증 정보가 만료 되었습니다. 다시 로그인 해주세요."),
    BLACK_LIST_NOT_FOUND("유효한 인증 이력이 존재하지 않습니다."),
    ACCOUNT_LOCK("3개월 간 미사용으로 잠긴 계정입니다."),
    ACCOUNT_BAN("비밀번호 5회 오류로 30분간 로그인이 제한됩니다."),
    GUEST_LOGIN_DENIED("현재 가입 대기중 입니다. 관리자에게 문의해 주세요."),
    LOGIN_REQUEST_INVALID("ID 또는 Password 가 입력되지 않았습니다."),
    USER_NOT_FOUND("유저가 존재하지 않습니다."),

    LOGIN_ERROR("로그인 처리 중 오류가 발생했습니다."),


    // JWT 인증 실패 목록
    UNABLE_AUTHORIZATION("인증 정보가 없거나 올바르지 않습니다."),
    EXPIRED_JWT("인증 정보가 만료 되었습니다. 다시 로그인 해주세요."),
    MALFORMED_JWT("인증 정보가 손상 되었습니다."),
    INVALID_SIGN("인증 정보의 서명이 올바르지 않습니다."),


    // Refresh Token
    REFRESH_UNABLE_AUTHORIZATION("인증 갱신 정보가 없거나 올바르지 않습니다."),
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