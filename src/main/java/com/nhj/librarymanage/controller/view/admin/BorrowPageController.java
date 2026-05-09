package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.BorrowRecordResponse;
import com.nhj.librarymanage.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Description(value = "도서 별 도서 대여 현황 조회")
    @GetMapping("/books/{id}/borrows")
    public String bookBorrowFragment(Model model, @PathVariable Long id, Pageable pageable) {
        PageResponse<BorrowRecordResponse.BookSummary> pageResponse = borrowRecordService.getBorrowRecordsByBook(id, pageable);
        model.addAttribute("borrowPageContent", pageResponse);

        return "admin/books/fragments/book-detail-borrows :: bookBorrows";
    }

}
