package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.dto.*;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.service.BorrowService;
import com.nhj.librarymanage.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BorrowController {

    private final BorrowService borrowService;
    private final ReturnService returnService;
    private final BorrowRecordService borrowRecordService;


    @Description(value = "회원 도서 대여 현황 조회")
    @GetMapping("/members/{memberId}/borrows")
    public ResponseEntity<ApiResponse> getMemberBorrowHistory(@PathVariable Long memberId, Pageable pageable) {
        // TODO 할일입니다.
        PageResponse<BorrowResponse.MemberHistory> pageResponse = borrowRecordService.getBorrowHistoryByMember(memberId, null, pageable);
        ApiResponse apiResponse = ApiResponse.result(pageResponse);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 대여")
    @PostMapping("/borrows")
    public ResponseEntity<Void> borrow(@RequestBody BorrowRequest.Create create) {
        borrowService.borrowBook(create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "도서 반납")
    @PostMapping("/returns")
    public ResponseEntity<Void> returnBook(@RequestBody ReturnRequest.Create create) {
        returnService.returnBook(create);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
