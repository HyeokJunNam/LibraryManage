package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.service.BookManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookManageService bookManageService;

    @Description(value = "도서 조회")
    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse> getBook(@PathVariable long id) {
        BookResponse.InfoDto infoDtoList = bookManageService.getBook(id);
        ApiResponse apiResponse = ApiResponse.result(infoDtoList);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 목록 조회")
    @GetMapping("/books")
    public ResponseEntity<ApiResponse> getBooks(@RequestBody(required = false) BookRequest.SearchDto searchDto) {
        List<BookResponse.InfoDto> infoDtoList = bookManageService.getBooks(searchDto);
        ApiResponse apiResponse = ApiResponse.result(infoDtoList);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 생성")
    @PostMapping("/books")
    public ResponseEntity<Void> createBook(@RequestBody BookRequest.CreateDto createDto) {
        bookManageService.createBook(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "도서 수정")
    @PutMapping("/books")
    public ResponseEntity<Void> updateBook(@RequestBody BookRequest.UpdateDto updateDto) {
        bookManageService.updateBook(updateDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "도서 삭제")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id) {
        bookManageService.deleteBook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
