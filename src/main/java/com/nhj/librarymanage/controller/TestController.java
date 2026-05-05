package com.nhj.librarymanage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {


    @PostMapping("/test")
    public void test() {

        //testService.push();

    }
}
