package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.*;
import com.nhj.librarymanage.model.table.*;
import com.nhj.librarymanage.service.BookService;
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
public class BorrowPageController {

    private final BorrowRecordService borrowRecordService;
    private final MemberService memberService;
    private final BookService bookService;

    @Description("대출/반납 처리")
    @GetMapping("/borrows/process")
    public String processBook() {
        return "admin/borrows/process/process";
    }


    @Description("도서 대출 현황")
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

        MemberSearchModalView.TABLE.applyTo(model);

        return "admin/borrows/process/modal/member-search-modal :: memberSearchResultPanel";
    }

    @Description("도서 검색(모달)")
    @GetMapping("/books/search")
    public String bookSearchModal(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BookResponse.Info> pageResponse = bookService.getBooks(searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        BookSearchModalView.TABLE.applyTo(model);

        return "admin/borrows/process/modal/book-search-modal :: bookSearchResultPanel";
    }

    @Description("반납용 회원 기준 도서 대출 목록")
    @GetMapping("/members/{memberId}/borrows/list")
    public String borrowListByMember(Model model, @PathVariable String memberId, BorrowHistoryRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        PageResponse<BorrowHistoryResponse.InfoByMember> pageResponse = borrowRecordService.getBorrowHistoryByMember(NumberParser.parseLong(memberId), searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("memberId", memberId);

        ReturnTableView.TABLE.applyTo(model);

        return "admin/borrows/process/fragments/book-return-panel :: bookReturnPanel";
    }



    // 도서 대출 현황

    @Description("도서 대출 목록")
    @GetMapping("/borrows/list")
    public String borrowList(Model model, BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BorrowHistoryResponse.Info> pageResponse = borrowRecordService.getBorrowHistory(searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        BorrowListTableView.TABLE.applyTo(model);

        return "admin/borrows/borrow-status/fragments/borrow-list-panel :: borrowListPanel";
    }

    @Description("도서 대출 연체 목록")
    @GetMapping("/borrows/overdue")
    public String overdueBorrowList(Model model, BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BorrowHistoryResponse.Info> pageResponse = borrowRecordService.getOverdueBorrowRecords(searchCondition, pageable);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        OverdueListTableView.TABLE.applyTo(model);

        return "admin/borrows/borrow-status/fragments/overdue-list-panel :: overdueListPanel";
    }





}
