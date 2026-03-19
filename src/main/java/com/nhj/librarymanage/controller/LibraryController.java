package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.domain.entity.BookEntity;
import com.nhj.librarymanage.service.BookManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class LibraryController {

    private final BookManageService bookManageService;

    @GetMapping("/")
    public String libraryMain(Model model) {
        List<BookResponse.InfoDto> infoDtoList = bookManageService.getBooks();
        model.addAttribute("books", infoDtoList);
        return "main";
    }


}
