package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.BorrowHistoryRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowHistoryResponse;
import com.nhj.librarymanage.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class BorrowPageController {

    private final BorrowRecordService borrowRecordService;

    @Description("대출/반납 처리 화면")
    @GetMapping("/borrows/process")
    public String processBook() {
        return "admin/borrows/process";
    }


    @Description("도서 대출 목록 View")
    @GetMapping("/borrows/status")
    public String borrowHistory() {
        return "admin/borrows/borrow-status";
    }

    @Description("도서 대출 목록")
    @GetMapping("/borrows/list")
    public String borrowList(Model model, BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BorrowHistoryResponse.Info> borrowHistory = borrowRecordService.getBorrowHistory(searchCondition, pageable);
        model.addAttribute("pageContent", borrowHistory);

        return "admin/borrows/fragments/borrow-list-panel :: borrowListPanel";
    }

    @Description("도서 대출 연체 목록")
    @GetMapping("/borrows/overdue")
    public String overdueBorrowList(Model model, BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BorrowHistoryResponse.Info> overdueBorrowRecords = borrowRecordService.getOverdueBorrowRecords(searchCondition, pageable);
        model.addAttribute("pageContent", overdueBorrowRecords);

        return "admin/borrows/fragments/overdue-borrow-list-panel :: overdueBorrowListPanel";
    }





}
