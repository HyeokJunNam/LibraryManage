package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.domain.model.dto.NotificationResponse;
import com.nhj.librarymanage.security.member.SecurityUser;
import com.nhj.librarymanage.service.BookService;
import com.nhj.librarymanage.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class LibraryController {

    private final BookService bookService;
    private final NotificationService notificationService;

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
    public String bookDetail(@AuthenticationPrincipal SecurityUser securityUser, Model model, @PathVariable Long bookId) {
        BookResponse.Detail detail = bookService.getBook(bookId);
        // TODO 수정 알지?
        Long memberId = securityUser != null ? securityUser.getId() : null;
        NotificationResponse.Status status = NotificationResponse.Status.from(notificationService.hasRequested(memberId, bookId));

        model.addAttribute("book", detail);
        model.addAttribute("notification", status);

        return "book-detail";
    }


}
