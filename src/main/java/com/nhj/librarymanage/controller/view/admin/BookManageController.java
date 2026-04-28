package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.PageContent;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.domain.model.dto.BorrowRecordResponse;
import com.nhj.librarymanage.service.BookService;
import com.nhj.librarymanage.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class BookManageController {

    private final BookService bookService;
    private final BorrowRecordService borrowRecordService;

    @Description("도서 관리 화면")
    @GetMapping("/books")
    public String books(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        PageContent<BookResponse.Summary> pageContent = bookService.getBooks(searchCondition, pageable);
        model.addAttribute("bookPageContent", pageContent);

        return "admin/books/books";
    }

    @Description("도서 관리 화면")
    @GetMapping("/books/{id}")
    public String bookDetail(Model model, @PathVariable Long id) {
        BookResponse.Detail detail = bookService.getBook(id);
        model.addAttribute("book", detail);

        return "admin/books/book-detail";
    }


    @Description("도서 추가 화면")
    @GetMapping("/books/new")
    public String newBook() {

        return "admin/books/books-new";
    }

}
