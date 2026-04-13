package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.annotations.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginPageController {

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

}
