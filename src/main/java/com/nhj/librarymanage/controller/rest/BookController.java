package com.nhj.librarymanage.controller.rest;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.BookItemRequest;
import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.service.BookItemService;
import com.nhj.librarymanage.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;
    private final BookItemService bookItemService;

    @Description(value = "도서 조회")
    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse> getBook(@PathVariable long id) {
        BookResponse.Detail detail = bookService.getBook(id);
        ApiResponse apiResponse = ApiResponse.result(detail);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 목록 조회")
    @GetMapping("/books")
    public ResponseEntity<ApiResponse> getBooks(@ModelAttribute BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BookResponse.Info> queryInfos = bookService.getBooks(searchCondition, pageable);
        ApiResponse apiResponse = ApiResponse.result(queryInfos);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 생성")
    @PostMapping("/books")
    public ResponseEntity<Void> createBook(@RequestBody List<BookRequest.Create> creates) {
        bookService.createBook(creates);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "도서 수정")
    @PutMapping("/books")
    public ResponseEntity<Void> updateBook(@RequestBody BookRequest.Update update) {
        bookService.updateBook(update);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "도서 삭제")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id) {
        bookService.deleteBook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "도서 재고 정보 등록")
    @PostMapping("/books/{bookId}/items")
    public ResponseEntity<Void> createBookItem(@PathVariable Long bookId, @RequestBody BookItemRequest.Create create) {
        bookItemService.createBookItem(bookId, create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
