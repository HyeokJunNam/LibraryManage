package com.nhj.librarymanage.controller.view.user;

import com.nhj.librarymanage.domain.annotations.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AuthController {

    @Description(value = "로그인 view")
    @GetMapping("/login")
    public String login() {
        return "user/auth/login";
    }

    @Description(value = "회원가입 view")
    @GetMapping("/signup")
    public String signupPage() {
        return "user/auth/signup";
    }

}
