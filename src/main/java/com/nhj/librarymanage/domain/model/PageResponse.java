package com.nhj.librarymanage.domain.model;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        PageMetaData pageMetaData
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                PageMetaData.from(page)
        );
    }

    // 스프링이나.. 그런 형태에 종속되지 않기 위함.. 이라고 한다면...
    public record PageMetaData(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
        public static PageMetaData from(Page<?> page) {
            return new PageMetaData(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast()
            );
        }
    }

}