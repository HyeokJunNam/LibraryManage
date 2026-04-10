package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.dto.MemberResponse;
import com.nhj.librarymanage.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class AdminController {

    private final MemberService memberService;

    @Description("대시보드")
    @GetMapping("/dashboard")
    public String dashboard() {

        return "admin/dashboard";
    }

    @Description("회원 관리 화면")
    @GetMapping("/members")
    public String members(Model model, Pageable pageable) {
        Page<MemberResponse.Info> infos = memberService.getMembers(pageable);
        model.addAttribute("members", infos);

        return "admin/members";
    }

    @Description("회원 정보 조회 화면")
    @GetMapping("/members/{id}")
    public String memberDetail(Model model, @PathVariable Long id) {
        MemberResponse.Info info = memberService.getMember(id);
        model.addAttribute("member", info);

        return "admin/member-detail";
    }

}
