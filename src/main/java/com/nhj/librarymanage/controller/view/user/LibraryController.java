package com.nhj.librarymanage.controller.view.user;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageRequest;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageResponse;
import com.nhj.librarymanage.domain.dto.temp.NotificationResponse;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.entity.MemberPrincipal;
import com.nhj.librarymanage.service.BookService;
import com.nhj.librarymanage.service.NotificationService;
import lombok.RequiredArgsConstructor;
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

    @Description("메인 검색 화면 (화면 이동)")
    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/library";
    }

    @Description("메인 검색 화면")
    @GetMapping("/library")
    public String libraryMain(Model model) {
        BookManageRequest.Search.addSearchFieldsTo(model);

        return "user/home/home";
    }

    @Description("도서 검색 결과 화면")
    @GetMapping("/library/books")
    public String bookSearchResultPage(Model model, @ModelAttribute BookManageRequest.Search search, Pageable pageable) {
        PageResponse<BookManageResponse.ListItem> pageResponse = bookService.getBooks(search, pageable);

        model.addAttribute("content", pageResponse.content());
        model.addAttribute("pageMetaData", pageResponse.pageMetaData());

        search.applyTo(model);

        return "user/book/books";
    }


    @Description("도서 상세 화면")
    @GetMapping("/library/books/{id}")
    public String bookDetail(Model model, @PathVariable Long id, @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        BookManageResponse.Detail detail = bookService.getBook(id);

        Long memberId = memberPrincipal.getId();
        NotificationResponse.Status status = NotificationResponse.Status.from(notificationService.hasRequested(memberId, id));

        model.addAttribute("book", detail);
        model.addAttribute("notification", status);

        return "user/book/book-detail";
    }


}
