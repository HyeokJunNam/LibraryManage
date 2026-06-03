package com.nhj.librarymanage.controller.view.user;

import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.dto.BorrowResponse;
import com.nhj.librarymanage.domain.dto.MyInfoResponse;
import com.nhj.librarymanage.domain.dto.PageResponse;
import com.nhj.librarymanage.domain.entity.MemberPrincipal;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class MyController {

    private final MemberService memberService;
    private final BorrowRecordService borrowRecordService;

    @GetMapping("/my")
    public String myPage(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MyInfoResponse.Detail detail = memberService.getMyInfo(memberPrincipal.getId());
        model.addAttribute("member", detail);


        return "user/my/my-info";
    }

    @GetMapping("/my/borrows")
    public String borrowListPanel(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal,
                                  BorrowRequest.SearchConditionByMember searchCondition,
                                  Pageable pageable,
                                  @RequestParam(required = false) BorrowStatus borrowStatus) {

        PageResponse<BorrowResponse.MyRecord> pageResponse = borrowRecordService.getMyBorrows(memberPrincipal.getId(), searchCondition, pageable);

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        if (borrowStatus == BorrowStatus.BORROWED) {
            return "user/my/fragments/current-borrow-panel :: currentBorrowPanel";
        }

        return "user/my/fragments/all-borrow-panel :: allBorrowPanel";
    }

}
