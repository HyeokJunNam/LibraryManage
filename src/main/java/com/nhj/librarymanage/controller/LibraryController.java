package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.service.BookManageService;
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

    private final BookManageService bookManageService;

    @GetMapping("/")
    public String libraryMain(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BookResponse.Info> infos = bookManageService.getBooks(searchCondition, pageable);
        model.addAttribute("books", infos);
        return "main";
    }

    @GetMapping("/library/books/{bookId}")
    public String bookDetail(@PathVariable Long bookId, Model model) {
        BookResponse.Detail detail = bookManageService.getBook(bookId);
        model.addAttribute("book", detail);
        return "book-detail";
    }


}
