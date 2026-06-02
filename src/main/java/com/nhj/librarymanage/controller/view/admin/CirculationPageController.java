package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.*;
import com.nhj.librarymanage.service.BookService;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.service.MemberService;
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
        return "admin/borrows/circulation/circulation";
    }

    @Description("대출 처리 패널")
    @GetMapping("/circulation/borrow")
    public String bookBorrowPanel() {
        return "admin/borrows/circulation/fragments/book-borrow :: bookBorrowPanel";
    }


    @Description("반납 처리 패널")
    @GetMapping("/circulation/return/members/{id}/borrows")
    public String bookReturnPanel(Model model, @PathVariable String id, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        PageResponse<BorrowResponse.Returnable> pageResponse = borrowRecordService.getReturnableBooksByMember(NumberParser.parseLong(id), searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("memberId", id);

        searchCondition.applySearchFields(model);

        return "admin/borrows/circulation/fragments/book-return :: bookReturnPanel";
    }


    @Description("도서 대출 목록")
    @GetMapping("/borrows/status")
    public String borrowHistory() {
        return "admin/borrows/borrow-status/borrow-status";
    }


    @Description("멤버 검색(모달)")
    @GetMapping("/members/search")
    public String memberSearchModal(Model model, @ModelAttribute MemberRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<MemberResponse.Info> pageResponse = memberService.getMembers(searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        searchCondition.applySearchFields(model);

        return "admin/borrows/circulation/modal/member-search-modal :: memberSearchResultPanel";
    }

    @Description("도서 검색(모달)")
    @GetMapping("/books/search")
    public String bookSearchModal(Model model, @ModelAttribute BookManageRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BookManageResponse.Info> pageResponse = bookService.getBooks(searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        searchCondition.applySearchFields(model);

        return "admin/borrows/circulation/modal/book-search-modal :: bookSearchResultPanel";
    }




    // 도서 대출 현황

    @Description("도서 대출 목록")
    @GetMapping("/borrows/list")
    public String borrowList(Model model, BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BorrowResponse.History> pageResponse = borrowRecordService.getBorrowHistory(searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        searchCondition.applySearchFields(model);

        return "admin/borrows/borrow-status/fragments/borrow-list-panel :: borrowListPanel";
    }

    @Description("도서 대출 연체 목록")
    @GetMapping("/borrows/overdue")
    public String overdueBorrowList(Model model, BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BorrowResponse.OverdueHistory> pageResponse = borrowRecordService.getOverdueBorrowRecords(searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        searchCondition.applySearchFields(model);

        return "admin/borrows/borrow-status/fragments/overdue-list-panel :: overdueListPanel";
    }





}
