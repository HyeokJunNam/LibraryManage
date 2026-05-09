package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    public record Detail(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            String description,
            String thumbnailUrl
    ) {
        public static Detail from(Book book) {
            return new Detail(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getDescription(),
                    book.getThumbnailUrl()
            );
        }
    }

    public record Summary(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            int stockQuantity,
            int availableQuantity
    ) {
        public static Summary from(Book book) {
            int availableQuantity = 0;
            int stockQuantity = 0;

            for (BookItem bookItem : book.getBookItems()) {
                stockQuantity++;

                if (bookItem.getStatus() == BookItemStatus.AVAILABLE
                        && bookItem.getBorrowRecord() == null) {
                    availableQuantity++;
                }
            }

            return new Summary(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    stockQuantity,
                    availableQuantity
            );
        }
    }
}