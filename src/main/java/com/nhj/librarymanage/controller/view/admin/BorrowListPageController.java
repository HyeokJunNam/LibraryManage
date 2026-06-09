package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.admin.book.BookModalResponse;
import com.nhj.librarymanage.domain.dto.admin.book.BookSearch;
import com.nhj.librarymanage.domain.dto.admin.circulation.BorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.circulation.BorrowResponse;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageResponse;
import com.nhj.librarymanage.service.AdminBookService;
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
public class BorrowListPageController {

    private final BorrowRecordService borrowRecordService;

    @Description("도서 대출 현황 화면")
    @GetMapping("/borrows/status")
    public String borrowStatus() {
        return "admin/borrow-status/borrow-status";
    }

    @Description("도서 대출 연체 목록 패널")
    @GetMapping("/borrows/overdue")
    public String overdueBorrowListPanel(Model model, BorrowRequest.Search search, Pageable pageable) {
        PageResponse<BorrowResponse.OverdueListItem> pageResponse = borrowRecordService.getOverdueBorrows(search, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        BorrowRequest.Search.applySearchFields(model);

        return "admin/borrow-status/fragments/overdue-list-panel :: overdueListPanel";
    }

    @Description("도서 대출 목록 패널")
    @GetMapping("/borrows/list")
    public String borrowListPanel(Model model, BorrowRequest.Search search, Pageable pageable) {
        PageResponse<BorrowResponse.ListItem> pageResponse = borrowRecordService.getBorrows(search, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        BorrowRequest.Search.applySearchFields(model);

        return "admin/borrow-status/fragments/borrow-list-panel :: borrowListPanel";
    }

}
