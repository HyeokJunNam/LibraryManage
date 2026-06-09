package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.admin.book.BookSearch;
import com.nhj.librarymanage.domain.dto.admin.book.BookModalResponse;
import com.nhj.librarymanage.domain.dto.admin.circulation.BorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.circulation.BorrowResponse;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageResponse;
import com.nhj.librarymanage.service.AdminBookService;
import com.nhj.librarymanage.service.MemberService;
import com.nhj.librarymanage.service.BookService;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.util.NumberParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class CirculationPageController {

    private final BorrowRecordService borrowRecordService;
    private final MemberService memberService;
    private final AdminBookService adminBookService;

    @Description("대출/반납 화면")
    @GetMapping("/circulation")
    public String circulationPage() {
        return "admin/circulation/circulation";
    }

    @Description("대출 처리 패널")
    @GetMapping("/circulation/borrow")
    public String borrowPanel() {
        return "admin/circulation/fragments/borrow-panel :: bookBorrowPanel";
    }


    @Description("반납 처리 패널")
    @GetMapping("/circulation/return/members/{id}/borrows")
    public String returnPanel(Model model, @PathVariable String id, MemberBorrowRequest.Search search, Pageable pageable) {
        PageResponse<BorrowResponse.ReturnableListItem> pageResponse = borrowRecordService.getReturnableBorrowsByMember(NumberParser.parseLong(id), search, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("memberId", id);

        MemberBorrowRequest.Search.applySearchFields(model);

        return "admin/circulation/fragments/return-panel :: bookReturnPanel";
    }


    @Description("멤버 검색(모달)")
    @GetMapping("/members/search")
    public String memberSearchModal(Model model, @ModelAttribute MemberManageRequest.Search search, Pageable pageable) {
        PageResponse<MemberManageResponse.ListItem> pageResponse = memberService.getMembers(search, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        MemberManageRequest.Search.applySearchFields(model);

        return "admin/circulation/modal/member-search-modal :: memberSearchResultPanel";
    }

    @Description("도서 검색(모달)")
    @GetMapping("/books/search")
    public String bookSearchModal(Model model, @ModelAttribute BookSearch search, Pageable pageable) {
        PageResponse<BookModalResponse.ListItem> pageResponse = adminBookService.getBooksForSearchModal(search, pageable);

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        search.applyTo(model);

        return "admin/circulation/modal/book-search-modal :: bookSearchResultPanel";
    }

}
