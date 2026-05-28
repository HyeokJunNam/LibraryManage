package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.*;
import com.nhj.librarymanage.model.vo.MemberStatistics;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.service.MemberService;
import com.nhj.librarymanage.util.NumberParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class MemberPageController {

    private final MemberService memberService;
    private final BorrowRecordService borrowRecordService;

    @Description("회원 목록 화면")
    @GetMapping("/members")
    public String memberListPage(Model model, @ModelAttribute MemberRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<MemberResponse.Info> pageResponse = memberService.getMembers(searchCondition, pageable);
        MemberStatistics memberStatistics = memberService.getMemberStatistics();

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("memberStatistics", memberStatistics);

        searchCondition.applySearchFields(model);

        return "admin/members/members";
    }

    @Description("회원 상세 화면")
    @GetMapping("/members/{id}")
    public String memberDetailPage(Model model, @PathVariable Long id) {
        MemberResponse.Detail content = memberService.getMember(id);
        model.addAttribute("content", content);

        return "admin/members/member-detail";
    }

    @Description("회원 별 도서 대출 목록 패널")
    @GetMapping("/members/{id}/borrows")
    public String memberBorrowHistoryPanel(Model model, @PathVariable Long id, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        PageResponse<BorrowResponse.MemberHistory> pageResponse = borrowRecordService.getBorrowHistoryByMember(id, searchCondition, pageable);

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("memberId", id);

        // TODO
        searchCondition.applySearchFields(model);

        return "admin/members/fragments/member-borrow-history :: memberBorrowHistoryPanel";
    }

    @Description("회원 정보 수정 화면")
    @GetMapping("/members/{id}/edit")
    public String memberEditPage(Model model, @PathVariable Long id) {

        return "admin/members/member-edit";
    }



}
