package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.model.dto.MemberResponse;
import com.nhj.librarymanage.security.member.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loginMember")
    public MemberResponse.Info loginMember(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        if (authenticatedUser == null) {
            return null;
        }

        return MemberResponse.Info.of(authenticatedUser.getLoginId(), authenticatedUser.getName());
    }

}
