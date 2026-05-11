package com.nhj.librarymanage.domain.model.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookRequest {

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class SearchCondition {
        private String isbn;
        private String title;
        private String author;
        private String publisher;
    }

    public record Create(
            List<Item> items
    ) {
        public record Item(
                String isbn,
                String title,
                String author,
                String publisher,
                String description,
                String thumbnailUrl
        ) {
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Update {
        private long id;
        private String name;

    }

}
