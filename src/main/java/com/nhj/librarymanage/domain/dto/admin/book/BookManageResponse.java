package com.nhj.librarymanage.domain.dto.admin.book;

import com.nhj.librarymanage.domain.entity.Book;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookManageResponse {

    public record ListItem(
            Long id,
            String location,
            String isbn,
            String title,
            String author,
            String publisher
    ) {
        public static ListItem from(Book book) {
            return new ListItem(
                    book.getId(),
                    book.getLocation(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher()
            );
        }
    }

    public record Detail(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            String description,
            String thumbnailUrl,
            String location
    ) {
        public static Detail from(Book book) {
            return new Detail(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getDescription(),
                    book.getThumbnailUrl(),
                    book.getLocation()
            );
        }
    }


}