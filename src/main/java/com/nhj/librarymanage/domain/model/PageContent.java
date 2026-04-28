package com.nhj.librarymanage.domain.model;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageContent<T>(
        List<T> content,
        PageMetadata pageMetadata
) {
    public static <T> PageContent<T> from(Page<T> page) {
        return new PageContent<>(
                page.getContent(),
                PageMetadata.from(page)
        );
    }
}