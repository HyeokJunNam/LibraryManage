package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Detail {
        private Long id;
        private String isbn;
        private String title;
        private String author;
        private String publisher;
        private String location;
        private String description;
        private String thumbnailUrl;

        private int stockQuantity;
        private int borrowedQuantity;
        private int availableQuantity;

        public static Detail from(Book book) {
            int stockQuantity = 0;
            int borrowedQuantity = 0;
            int availableQuantity = 0;

            for (BookItem bookItem : book.getBookItems()) {
                stockQuantity++;
                if (bookItem.getStatus() == BookItemStatus.AVAILABLE) {
                    if (bookItem.getBorrowRecord() != null) {
                        borrowedQuantity++;
                    }
                    else {
                        availableQuantity++;
                    }
                }
            }

            return Detail.builder()
                    .id(book.getId())
                    .isbn(book.getIsbn())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .publisher(book.getPublisher())
                    .description(book.getDescription())
                    .location(book.getLocation())
                    .thumbnailUrl(book.getThumbnailUrl())
                    .stockQuantity(stockQuantity)
                    .borrowedQuantity(borrowedQuantity)
                    .availableQuantity(availableQuantity)
                    .build();

        }
    }

    public record Summary(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            String location,
            int stockQuantity,
            int availableQuantity
    ) {
        public static Summary from(Book book) {
            int availableQuantity = 0;
            int stockQuantity = 0;

            for (BookItem bookItem : book.getBookItems()) {
                stockQuantity++;

                if (bookItem.getStatus() == BookItemStatus.AVAILABLE && bookItem.getBorrowRecord() == null) {
                    availableQuantity++;
                }
            }

            return new Summary(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getLocation(),
                    stockQuantity,
                    availableQuantity
            );
        }
    }

}
