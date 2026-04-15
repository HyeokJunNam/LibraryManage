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
public class BorrowManageController {

    private final BookService bookService;

    @Description("대출/반납 처리 화면")
    @GetMapping("/borrows/process")
    public String books(Model model, @ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {


        return "admin/borrows/process";
    }

}
