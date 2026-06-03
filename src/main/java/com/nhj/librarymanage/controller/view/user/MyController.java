package com.nhj.librarymanage.controller.view.user;

import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.member.borrow.MyBorrowResponse;
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
                                  MemberBorrowRequest.Search search,
                                  Pageable pageable,
                                  @RequestParam(required = false) BorrowStatus borrowStatus) {

        PageResponse<MyBorrowResponse.ListItem> pageResponse = borrowRecordService.getMyBorrows(memberPrincipal.getId(), search, pageable);

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        if (borrowStatus == BorrowStatus.BORROWED) {
            return "user/my/fragments/current-borrow-panel :: currentBorrowPanel";
        }

        return "user/my/fragments/all-borrow-panel :: allBorrowPanel";
    }

}
