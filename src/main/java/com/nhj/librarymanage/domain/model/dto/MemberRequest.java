package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.security.member.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequest {

    public record SearchCondition(
            String name,
            String memberNo,
            String email
    ) {
    }

    // 생성 요청
    public record Create(
            String loginId,
            String password,
            Role role,
            String name,
            String email,
            String signupToken
    ) {
    }

    // 수정 요청

    public record Update(
            Long id,
            String name
    ) {
    }

}
