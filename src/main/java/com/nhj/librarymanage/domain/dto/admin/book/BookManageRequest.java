package com.nhj.librarymanage.domain.dto.admin.book;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookManageRequest {

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
