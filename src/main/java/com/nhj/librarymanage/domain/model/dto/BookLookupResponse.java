package com.nhj.librarymanage.domain.model.dto;

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