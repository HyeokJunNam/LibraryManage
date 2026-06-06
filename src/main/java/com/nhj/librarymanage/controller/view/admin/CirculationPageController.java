package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageRequest;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageResponse;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowResponse;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberManageResponse;
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
    private final BookService bookService;

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


    @Description("도서 대출 현황 화면")
    @GetMapping("/borrows/status")
    public String borrowStatus() {
        return "admin/borrow-status/borrow-status";
    }


    @Description("멤버 검색(모달)")
    @GetMapping("/members/search")
    public String memberSearchModal(Model model, @ModelAttribute MemberManageRequest.Search search, Pageable pageable) {
        PageResponse<MemberManageResponse.Info> pageResponse = memberService.getMembers(search, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        MemberManageRequest.Search.applySearchFields(model);

        return "admin/circulation/modal/member-search-modal :: memberSearchResultPanel";
    }

    @Description("도서 검색(모달)")
    @GetMapping("/books/search")
    public String bookSearchModal(Model model, @ModelAttribute BookManageRequest.Search search, Pageable pageable) {
        PageResponse<BookManageResponse.Info> pageResponse = bookService.getBooks(search, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        search.applyTo(model);

        return "admin/circulation/modal/book-search-modal :: bookSearchResultPanel";
    }


    // 도서 대출 현황

    @Description("도서 대출 목록 패널")
    @GetMapping("/borrows/list")
    public String borrowListPanel(Model model, BorrowRequest.Search search, Pageable pageable) {
        PageResponse<BorrowResponse.ListItem> pageResponse = borrowRecordService.getBorrows(search, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        BorrowRequest.Search.applySearchFields(model);

        return "admin/borrow-status/fragments/borrow-list-panel :: borrowListPanel";
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





}
