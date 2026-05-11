package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.ApiResponse;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.BookCopyRequest;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.service.BookCopyService;
import com.nhj.librarymanage.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BookController {

    private final BookService bookService;
    private final BookCopyService bookCopyService;

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
        PageResponse<BookResponse.Info> books = bookService.getBooks(searchCondition, pageable);
        ApiResponse apiResponse = ApiResponse.result(books);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 생성")
    @PostMapping("/books")
    public ResponseEntity<HttpStatus> createBook(@RequestBody BookRequest.Create create) {
        bookService.createBooks(create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "도서 수정")
    @PutMapping("/books")
    public ResponseEntity<HttpStatus> updateBook(@RequestBody BookRequest.Update update) {
        bookService.updateBook(update);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "도서 삭제")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "도서 재고 정보 등록 및 수정, 삭제 ")
    @PostMapping("/books/{bookId}/copies/batch")
    public ResponseEntity<HttpStatus> createBookItem(@PathVariable Long bookId, @RequestBody BookCopyRequest.Upsert upsert) {
        bookCopyService.upsetBookCopy(bookId, upsert);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
