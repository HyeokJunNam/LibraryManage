package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.ApiResponse;
import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.model.PageContent;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowRecordResponse;
import com.nhj.librarymanage.service.BorrowRecordService;
import com.nhj.librarymanage.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BorrowController {

    private final BorrowService borrowService;
    private final BorrowRecordService borrowRecordService;

    /*@Description(value = "전체 도서 대여 현황 조회")
    @GetMapping("/borrows")
    public ResponseEntity<ApiResponse> getBorrowHistory(@ModelAttribute BorrowRequest.Param param, Pageable pageable) {
        Page<BorrowRecordResponse.InfoList> infos = borrowService.getBorrowRecords(param, pageable);
        ApiResponse apiResponse = ApiResponse.result(infos);

        return ResponseEntity.ok().body(apiResponse);
    }

    */

    @Description(value = "회원 도서 대여 현황 조회")
    @GetMapping("/members/{memberId}/borrows")
    public ResponseEntity<ApiResponse> getMemberBorrowHistory(@PathVariable Long memberId, Pageable pageable) {
        PageContent<BorrowRecordResponse.MemberSummary> pageContent = borrowRecordService.getBorrowRecordsByMember(memberId, pageable);
        ApiResponse apiResponse = ApiResponse.result(pageContent);

        return ResponseEntity.ok().body(apiResponse);
    }

    @Description(value = "도서 대여")
    @PostMapping("/borrows")
    public ResponseEntity<Void> borrow(@RequestBody BorrowRequest.Borrow borrow) {
        borrowService.borrow(borrow);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Description(value = "도서 반납")
    @PostMapping("/returns")
    public ResponseEntity<Void> returnBook(@RequestBody BorrowRequest.ReturnBook returnBook) {
        borrowService.returnBook(returnBook);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
