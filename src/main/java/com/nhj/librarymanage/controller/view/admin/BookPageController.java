package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.code.AdminPageOptions;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.*;
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

    @Description("도서 관리 화면")
    @GetMapping("/books")
    public String books(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BookResponse.Info> pageResponse = bookService.getBooks(searchCondition, pageable);
        BorrowStatistics borrowStatistics = borrowRecordService.getBorrowStatistics();

        model.addAttribute("books", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("borrowStatistics", borrowStatistics);

        return "admin/books/books";
    }

    @Description("도서 상세 화면")
    @GetMapping("/books/{id}")
    public String bookDetail(Model model, @PathVariable Long id) {
        BookResponse.Detail detail = bookService.getBook(id);
        model.addAttribute("book", detail);

        return "admin/books/book-detail";
    }

    @Description(value = "도서 재고 현황 조회")
    @GetMapping("/books/{id}/copies")
    public String bookItemFragment(Model model, @PathVariable Long id, Pageable pageable) {
        PageResponse<BookCopyResponse.Info> pageResponse = bookCopyService.getBookCopies(id, pageable); // 여기서 레코드 한번 더 조회 타는거 있음. 근데 1번 더타는건 그래프 탐색 특성 상 허용되어야 함

        model.addAttribute("bookId", id);
        model.addAttribute("bookCopies", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());
        model.addAttribute("options", AdminPageOptions.options());

        return "admin/books/fragments/book-detail-copies :: bookCopies";
    }

    @Description(value = "도서 별 도서 대출 현황 조회")
    @GetMapping("/books/{id}/borrows")
    public String bookBorrowFragment(Model model, @PathVariable Long id, Pageable pageable) {
        PageResponse<BorrowHistoryResponse.InfoByBook> pageResponse = borrowRecordService.getBorrowHistoryByBook(id, pageable);

        model.addAttribute("borrows", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        return "admin/books/fragments/book-detail-borrows :: bookBorrows";
    }

    @Description("도서 등록 화면")
    @GetMapping("/books/new")
    public String newBook() {

        return "admin/books/books-new";
    }

    @Description("도서 정보 수정 화면")
    @GetMapping("/books/{id}/edit")
    public String editBook(Model model, @PathVariable Long id) {
        BookResponse.Detail detail = bookService.getBook(id);
        model.addAttribute("book", detail);

        return "admin/books/books-edit";
    }

}
