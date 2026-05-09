package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.code.AdminPageOptions;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.BookItemResponse;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.domain.model.dto.BorrowStatistics;
import com.nhj.librarymanage.service.BookItemService;
import com.nhj.librarymanage.service.BookService;
import com.nhj.librarymanage.service.BorrowRecordService;
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
public class BookPageController {

    private final BookService bookService;
    private final BookItemService bookItemService;
    private final BorrowRecordService borrowRecordService;

    @Description("도서 관리 화면")
    @GetMapping("/books")
    public String books(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        PageResponse<BookResponse.Summary> pageResponse = bookService.getBooks(searchCondition, pageable);
        BorrowStatistics borrowStatistics = borrowRecordService.getBorrowStatistics();

        model.addAttribute("bookPageContent", pageResponse);
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

    @Description(value = "도서 재고 현황 조회")
    @GetMapping("/books/{id}/items")
    public String bookItemFragment(Model model, @PathVariable Long id, Pageable pageable) {
        PageResponse<BookItemResponse.Summary> pageResponse = bookItemService.getBookItems(id, pageable);

        model.addAttribute("bookId", id);
        model.addAttribute("itemPageResponse", pageResponse);
        model.addAttribute("options", AdminPageOptions.options());

        return "admin/books/fragments/book-detail-items :: bookItems";
    }

}
