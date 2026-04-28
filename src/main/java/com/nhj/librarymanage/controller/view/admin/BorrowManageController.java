package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.PageContent;
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
public class BorrowManageController {

    private final BorrowRecordService borrowRecordService;

    @Description("대출/반납 처리 화면")
    @GetMapping("/borrows/process")
    public String processBook() {
        return "admin/borrows/process";
    }

    @Description(value = "도서 별 도서 대여 현황 조회")
    @GetMapping("/members/{id}/borrows")
    public String memberBorrowFragment(Model model, @PathVariable Long id, Pageable pageable) {
        PageContent<BorrowRecordResponse.BookSummary> pageContent = borrowRecordService.getBorrowRecordsByBook(id, pageable);
        model.addAttribute("borrowPageContent", pageContent);

        return "admin/books/fragments/book-detail-borrows :: bookBorrows";
    }

}
