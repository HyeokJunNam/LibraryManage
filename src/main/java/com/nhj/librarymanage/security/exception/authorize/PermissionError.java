package com.nhj.librarymanage.security.exception.authorize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PermissionError {

    //  S   0   0   1
    //  |   |   |   |
    //  |   ---------- Security Response Code

    UNEXPECTED_PERMISSION_ERROR("예상치 못한 권한 처리 오류가 발생했습니다."),

    ACCESS_DENIED("리소스에 접근 가능한 역할이 아니거나, 요청을 처리할 수 있는 권한 없습니다."),

    INVALID_ROLE("유효하지 않은 권한입니다."),

    NOT_PRINCIPAL_ACCESS_DENIED("본인 이외 리소스에 접근할 수 없습니다."),
    ROLE_NOT_EXIST("인증 정보 내 역할이 존재하지 않습니다."),
    PRIVILEGE_NOT_EXIST("인증 정보 내 권한이 존재하지 않습니다."),
    PRIVILEGE_INVALID("더이상 사용되지 않거나 유효하지 않은 권한입니다."),
    PRIVILEGE_NOT_BLANK("사용자에게 권한은 반드시 제공되어야 합니다."),
    ;

    private final String message;

    public String getCode() {
        return HttpStatus.FORBIDDEN.name() + "_" + name();
    }

}