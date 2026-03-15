package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.security.member.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequest {

    @AllArgsConstructor
    @Getter
    public static class ParamDto {

    }

    // 생성 요청
    @AllArgsConstructor
    @Getter
    public static class CreateDto {
        private String loginId;
        private String password;
        private Role role;
        private String name;

    }

    // 수정 요청
    @AllArgsConstructor
    @Getter
    public static class UpdateDto {
        private long id;
        private String name;

    }

}
