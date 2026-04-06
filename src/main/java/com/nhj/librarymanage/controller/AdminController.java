package com.nhj.librarymanage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AdminController {

    @GetMapping("/admin/dashboard")
    public String dashboard() {

        return "admin/dashboard";
    }

    @GetMapping("/admin/members")
    public String members() {

        return "admin/members";
    }

}
