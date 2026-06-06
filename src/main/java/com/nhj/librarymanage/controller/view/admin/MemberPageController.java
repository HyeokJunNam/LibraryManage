package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageResponse;
import com.nhj.librarymanage.model.vo.MemberStatistics;
import com.nhj.librarymanage.service.MemberService;
import com.nhj.librarymanage.service.BorrowRecordService;
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
    public String memberListPage(Model model, @ModelAttribute MemberManageRequest.Search search, Pageable pageable) {
        PageResponse<MemberManageResponse.Info> pageResponse = memberService.getMembers(search, pageable);
        MemberStatistics memberStatistics = memberService.getMemberStatistics();

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("memberStatistics", memberStatistics);

        MemberManageRequest.Search.applySearchFields(model);

        return "admin/member/members";
    }

    @Description("회원 상세 화면")
    @GetMapping("/members/{id}")
    public String memberDetailPage(Model model, @PathVariable Long id) {
        MemberManageResponse.Detail content = memberService.getMember(id);
        model.addAttribute("content", content);

        return "admin/member/member-detail";
    }

    @Description("회원 별 도서 대출 목록 패널")
    @GetMapping("/members/{id}/borrows")
    public String borrowListPanel(Model model, @PathVariable Long id, MemberBorrowRequest.Search search, Pageable pageable) {
        PageResponse<MemberBorrowResponse.ListItem> pageResponse = borrowRecordService.getBorrowsByMember(id, search, pageable);

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("memberId", id);

        // TODO
        MemberBorrowRequest.Search.applySearchFields(model);

        return "admin/member/fragments/member-borrow-history :: memberBorrowHistoryPanel";
    }

    @Description("회원 정보 수정 화면")
    @GetMapping("/members/{id}/edit")
    public String editMemberPage(Model model, @PathVariable Long id) {

        return "admin/member/member-edit";
    }



}
