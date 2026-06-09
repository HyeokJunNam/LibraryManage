package com.nhj.librarymanage.controller.view.user;

import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.member.borrow.MyBorrowResponse;
import com.nhj.librarymanage.domain.dto.member.info.MyInfoRequest;
import com.nhj.librarymanage.domain.dto.member.info.MyInfoResponse;
import com.nhj.librarymanage.domain.entity.MemberPrincipal;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class MyBorrowController {

    private final BorrowRecordService borrowRecordService;


    @GetMapping("/my/borrows")
    public String borrowListPage(Model model, @AuthenticationPrincipal MemberPrincipal memberPrincipal,
                                  MemberBorrowRequest.Search search,
                                  Pageable pageable,
                                  @RequestParam(required = false) BorrowStatus borrowStatus) {

        PageResponse<MyBorrowResponse.ListItem> pageResponse = borrowRecordService.getMyBorrows(memberPrincipal.getId(), search, pageable);

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());


        return "user/my/borrow-list";
    }

}
