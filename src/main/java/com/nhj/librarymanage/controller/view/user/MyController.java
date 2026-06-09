package com.nhj.librarymanage.controller.view.user;

import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.member.borrow.MyBorrowResponse;
import com.nhj.librarymanage.domain.dto.member.info.MyInfoRequest;
import com.nhj.librarymanage.domain.dto.member.info.MyInfoResponse;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.entity.MemberPrincipal;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class MyController {

    private final MemberService memberService;
    private final BorrowRecordService borrowRecordService;

    @GetMapping("/my")
    public String myInfoPage(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getId();

        MyInfoResponse.Detail detail = memberService.getMyInfo(memberId);
        MyInfoResponse.BorrowStatistics borrowStatistics = borrowRecordService.getBorrowStatisticsByMemberId(memberId);

        model.addAttribute("member", detail);
        model.addAttribute("borrowStatistics", borrowStatistics);


        return "user/my/my-info";
    }

    @GetMapping("/my/edit/password-check")
    public String passwordCheckForm() {
        return "user/my/my-info-password-check";
    }

    @PostMapping("/my/edit/password-check")
    public String passwordCheck(@AuthenticationPrincipal MemberPrincipal memberPrincipal, @RequestParam String currentPassword, Model model) {
        Long memberId = memberPrincipal.getId();
        boolean matched = memberService.matchesCurrentPassword(memberId, currentPassword);

        if (!matched) {
            model.addAttribute(
                    "errorMessage",
                    "비밀번호가 일치하지 않습니다."
            );

            return "user/my/my-info-password-check";
        }


        return "redirect:/my/edit";
    }

    @GetMapping("/my/edit")
    public String editMyInfo(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        Long memberId = memberPrincipal.getId();
        MyInfoResponse.Detail detail = memberService.getMyInfo(memberId);

        model.addAttribute("member", detail);

        return "user/my/my-info-edit";
    }

    @PostMapping("/my/edit")
    public String edit(@AuthenticationPrincipal MemberPrincipal memberPrincipal, @ModelAttribute MyInfoRequest.Update update) {
        Long memberId = memberPrincipal.getId();
        memberService.updateMyInfo(memberId, update);


        return "redirect:/my";
    }

}
