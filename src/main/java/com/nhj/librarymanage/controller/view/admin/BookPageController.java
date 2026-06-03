package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.dto.*;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import com.nhj.librarymanage.service.BookCopyService;
import com.nhj.librarymanage.service.BookService;
import com.nhj.librarymanage.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class BookPageController {

    private final BookService bookService;
    private final BookCopyService bookCopyService;
    private final BorrowRecordService borrowRecordService;

    @Description("도서 관리 도서 목록 화면")
    @GetMapping("/books")
    public String bookListPage(Model model, @ModelAttribute BookManageRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BookManageResponse.Info> pageResponse = bookService.getBooks(searchCondition, pageable);
        BorrowStatistics borrowStatistics = borrowRecordService.getBorrowStatistics();

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("borrowStatistics", borrowStatistics);

        searchCondition.applySearchFields(model);

        return "admin/books/books";
    }

    @Description("도서 관리 도서 상세 화면")
    @GetMapping("/books/{id}")
    public String bookDetailPage(Model model, @PathVariable Long id) {
        BookManageResponse.Detail detail = bookService.getBook(id);
        model.addAttribute("content", detail);

        return "admin/books/book-detail";
    }

    @Description(value = "도서 재고 목록 패널")
    @GetMapping("/books/{id}/copies")
    public String bookCopyListPanel(Model model, @PathVariable Long id, Pageable pageable) {
        PageResponse<BookCopyResponse.Info> pageResponse = bookCopyService.getBookCopies(id, pageable); // 여기서 레코드 한번 더 조회 타는거 있음. 근데 1번 더타는건 그래프 탐색 특성 상 허용되어야 함

        model.addAttribute("bookId", id);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("conditionOptions", BookCopyCondition.options());

        return "admin/books/fragments/book-detail-copies :: bookCopies";
    }

    @Description(value = "도서 별 도서 대출 목록 패널")
    @GetMapping("/books/{id}/borrows")
    public String bookBorrowHistoryPanel(Model model, @PathVariable Long id, @ModelAttribute BorrowRequest.SearchConditionByBook searchCondition, Pageable pageable) {
        PageResponse<BorrowResponse.BookHistory> pageResponse = borrowRecordService.getBorrowsByBook(id, searchCondition, pageable);

        model.addAttribute("bookId", id);
        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        searchCondition.applySearchFields(model);

        return "admin/books/fragments/book-detail-borrows :: bookBorrows";
    }

    @Description("도서 등록 화면")
    @GetMapping("/books/new")
    public String newBookPage() {

        return "admin/books/books-new";
    }

    @Description("도서 수정 화면")
    @GetMapping("/books/{id}/edit")
    public String editBookPage(Model model, @PathVariable Long id) {
        BookManageResponse.Detail detail = bookService.getBook(id);
        model.addAttribute("book", detail);

        return "admin/books/books-edit";
    }

}
