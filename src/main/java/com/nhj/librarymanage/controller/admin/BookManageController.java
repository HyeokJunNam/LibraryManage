package com.nhj.librarymanage.controller.admin;

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
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class BookManageController {

    private final BookService bookService;

    @Description("도서 관리 화면")
    @GetMapping("/books")
    public String books(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BookResponse.Info> infos = bookService.getBooks(searchCondition, pageable);
        model.addAttribute("books", infos);

        return "admin/books";
    }

    @Description("회원 정보 조회 화면")
    @GetMapping("/books/{id}")
    public String bookDetail(Model model, @PathVariable Long id) {

        return null;
    }

}
