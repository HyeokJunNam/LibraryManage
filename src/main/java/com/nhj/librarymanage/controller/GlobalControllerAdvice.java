package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.dto.MemberResponse;
import com.nhj.librarymanage.security.member.SecurityUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loginMember")
    public MemberResponse.Info loginMember(@AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null) {
            return null;
        }

        return MemberResponse.Info.builder()
                .loginId(securityUser.getUsername())
                .name(securityUser.getName())
                .build();
    }

}
