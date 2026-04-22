package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    @Builder(access = AccessLevel.PRIVATE)
    public record Detail(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            String description,
            String location,
            String thumbnailUrl,
            int availableCount,
            boolean canBorrow
    ) {
        public static Detail from(Book book) {
            int availableCount = 0;

            for (BookItem bookItem : book.getBookItems()) {
                if (bookItem.getStatus() == BookItemStatus.AVAILABLE && bookItem.getBorrowRecord() == null) {
                    availableCount++;
                }
            }

            boolean canBorrow = availableCount >= 1;

            return Detail.builder()
                    .id(book.getId())
                    .isbn(book.getIsbn())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .publisher(book.getPublisher())
                    .description(book.getDescription())
                    .location(book.getLocation())
                    .thumbnailUrl(book.getThumbnailUrl())
                    .availableCount(availableCount)
                    .canBorrow(canBorrow)
                    .build();
        }
    }

    public record Info(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            String location,
            int stockQuantity,
            int availableQuantity
    ) {
        public static Info from(Book book) {
            int availableQuantity = 0;
            int stockQuantity = 0;

            for (BookItem bookItem : book.getBookItems()) {
                stockQuantity++;

                if (bookItem.getStatus() == BookItemStatus.AVAILABLE && bookItem.getBorrowRecord() == null) {
                    availableQuantity++;
                }
            }

            return new Info(
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
