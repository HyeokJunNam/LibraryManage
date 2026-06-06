package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.domain.dto.admin.circulation.BorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.circulation.ReturnRequest;
import com.nhj.librarymanage.service.BorrowService;
import com.nhj.librarymanage.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BorrowController {

    private final BorrowService borrowService;
    private final ReturnService returnService;

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
