package com.nhj.librarymanage.domain.model;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        PageMetadata pageMetadata,
        Object attribute
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                PageMetadata.from(page),
                null
        );
    }

    public static <T> PageResponse<T> of(Page<T> page, Object attribute) {
        return new PageResponse<>(
                page.getContent(),
                PageMetadata.from(page),
                attribute
        );
    }
}