package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class LibraryController {

    private final BookService bookService;

    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/library";
    }

    @Description("메인 검색 화면")
    @GetMapping("/library")
    public String libraryMain() {
        return "main";
    }

    @Description("도서 목록 화면")
    @GetMapping("/library/books")
    public String bookList(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BookResponse.Info> infos = bookService.getBooks(searchCondition, pageable);
        model.addAttribute("books", infos);
        return "book-list";
    }


    @Description("도서 상세 화면")
    @GetMapping("/library/books/{bookId}")
    public String bookDetail(Model model, @PathVariable Long bookId) {
        BookResponse.Detail detail = bookService.getBook(bookId);
        model.addAttribute("book", detail);
        return "book-detail";
    }


}
