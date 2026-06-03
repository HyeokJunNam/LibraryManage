package com.nhj.librarymanage.domain.dto.temp;

import lombok.Builder;

@Builder
public record BookLookupResponse (
        String isbn,
        String title,
        String author,
        String publisher,
        String description
) {
}