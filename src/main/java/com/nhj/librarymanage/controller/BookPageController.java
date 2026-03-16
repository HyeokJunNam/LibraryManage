package com.nhj.librarymanage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/library")
@Controller
public class BookPageController {

    @GetMapping("/book-list")
    public String bookListPage() {
        return "library/book-list";
    }

}
