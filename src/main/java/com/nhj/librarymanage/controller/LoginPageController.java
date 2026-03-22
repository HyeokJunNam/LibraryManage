package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.service.MemberManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class LoginPageController {

    private final MemberManageService memberManageService;

    @Description(value = "로그인 view")
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @Description(value = "회원가입 view")
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @Description(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody MemberRequest.Create create) {
        memberManageService.createMember(create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
