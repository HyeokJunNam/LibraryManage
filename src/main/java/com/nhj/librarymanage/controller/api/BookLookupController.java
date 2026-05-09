package com.nhj.librarymanage.controller.api;

import com.nhj.librarymanage.domain.model.ApiResponse;
import com.nhj.librarymanage.domain.model.dto.BookLookupResponse;
import com.nhj.librarymanage.service.BookMetadataLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BookLookupController {

    private final BookMetadataLookupService bookMetadataLookupService;

    @GetMapping("/books/lookup")
    public ResponseEntity<ApiResponse> lookupBook(@RequestParam String isbn) {
        BookLookupResponse response = bookMetadataLookupService.lookupByIsbn(isbn);
        ApiResponse apiResponse = ApiResponse.result(response);

        return ResponseEntity.ok().body(apiResponse);
    }
}
