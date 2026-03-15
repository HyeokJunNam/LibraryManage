package com.nhj.librarymanage.controller;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.dto.BorrowResponse;
import com.nhj.librarymanage.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BorrowController {

    private final BorrowService borrowService;

    @Description(value = "도서 대여 현황 조회")
    @GetMapping("/borrows")
    public ResponseEntity<ApiResponse> getBorrowHistories(@ModelAttribute BorrowRequest.ParamDto paramDto, Pageable pageable) {
        Page<BorrowResponse.InfoDto> infoDtoList = borrowService.getBorrowHistories(paramDto, pageable);
        ApiResponse apiResponse = ApiResponse.result(infoDtoList);

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
