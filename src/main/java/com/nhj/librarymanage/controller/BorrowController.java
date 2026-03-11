package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BorrowController {

    private final BorrowService borrowService;

    @Description(value = "도서 대여 현황 조회")
    @GetMapping("/borrows")
    public ResponseEntity<ApiResponse> getBorrows() {
        //BookResponse.InfoDto infoDtoList = bookManageService.getBook(id);
        ApiResponse apiResponse = ApiResponse.result(null);

        return ResponseEntity.ok().body(apiResponse);
    }


    @Description(value = "도서 대여")
    @PostMapping("/borrows")
    public ResponseEntity<Void> borrow(@RequestBody BorrowRequest.BorrowDto borrowDto) {
        borrowService.borrow(borrowDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "도서 반납")
    @PostMapping("/returns")
    public ResponseEntity<Void> returnBook(@RequestBody BorrowRequest.ReturnBookDto returnBookDto) {
        borrowService.returnBook(returnBookDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
