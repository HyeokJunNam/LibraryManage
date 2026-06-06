package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.admin.book.BookSearch;
import com.nhj.librarymanage.domain.dto.admin.common.ApiResponse;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.book.BookCopyRequest;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageRequest;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageResponse;
import com.nhj.librarymanage.service.AdminBookService;
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

    private final AdminBookService adminBookService;
    private final BookCopyService bookCopyService;

    @Description(value = "도서 재고 정보 등록 및 수정, 삭제")
    @PostMapping("/books/{bookId}/copies/batch")
    public ResponseEntity<HttpStatus> createBookItem(@PathVariable Long bookId, @RequestBody BookCopyRequest.Upsert upsert) {
        bookCopyService.upsetBookCopy(bookId, upsert);

        return ResponseEntity.status(HttpStatus.OK).build();
    }



    // 현재 미사용 추정 *하단*







    @Description(value = "도서 조회")
    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse> getBook(@PathVariable long id) {
        BookManageResponse.Detail detail = adminBookService.getBook(id);
        ApiResponse apiResponse = ApiResponse.result(detail);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 목록 조회")
    @GetMapping("/books")
    public ResponseEntity<ApiResponse> getBooks(@ModelAttribute BookSearch search, Pageable pageable) {
        PageResponse<BookManageResponse.ListItem> books = adminBookService.getBooks(search, pageable);
        ApiResponse apiResponse = ApiResponse.result(books);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 생성")
    @PostMapping("/books")
    public ResponseEntity<HttpStatus> createBook(@RequestBody BookManageRequest.Create create) {
        adminBookService.createBooks(create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "도서 수정")
    @PutMapping("/books")
    public ResponseEntity<HttpStatus> updateBook(@RequestBody BookManageRequest.Update update) {
        adminBookService.updateBook(update);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Description(value = "도서 삭제")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable Long id) {
        adminBookService.deleteBook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
