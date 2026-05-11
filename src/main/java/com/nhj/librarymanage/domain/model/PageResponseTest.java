package com.nhj.librarymanage.domain.model;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResponseTest<T>(
        T content,
        PageMetadata page
) {
    public record PageMetadata(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
        public static PageMetadata from(Page<?> page) {
            return new PageMetadata(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast()
            );
        }
    }

    public static <E, C> PageResponseTest<C> from(
            Page<E> page,
            Function<List<E>, C> converter
    ) {
        return new PageResponseTest<>(
                converter.apply(page.getContent()),
                PageMetadata.from(page)
        );
    }
}