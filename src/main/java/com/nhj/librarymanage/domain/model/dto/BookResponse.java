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
            String thumbnailUrl,

            int stockQuantity,
            int borrowedQuantity,
            int availableQuantity,

            List<BookItemEntry> bookItems
    ) {

        public record BookItemEntry(
                Long bookItemId,
                String location,
                EnumOption<BorrowStatus> borrowStatus,
                EnumOption<BookItemStatus> bookItemStatus,
                LocalDateTime createdAt
        ) {
        }

        public static Detail from(Book book) {
            int stockQuantity = 0;
            int borrowedQuantity = 0;
            int availableQuantity = 0;

            List<BookItemEntry> bookItemEntries = new ArrayList<>();

            for (BookItem bookItem : book.getBookItems()) {
                stockQuantity++;

                switch (bookItem.getBorrowStatus()) {
                    case AVAILABLE -> availableQuantity++;
                    case BORROWED -> borrowedQuantity++;
                }

                BookItemEntry bookItemEntry = new BookItemEntry(
                        bookItem.getId(),
                        bookItem.getLocation(),
                        EnumOption.from(bookItem.getBorrowStatus()),
                        EnumOption.from(bookItem.getStatus()),
                        bookItem.getCreatedAt()
                );

                bookItemEntries.add(bookItemEntry);
            }

            return new Detail(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getDescription(),
                    book.getThumbnailUrl(),
                    stockQuantity,
                    borrowedQuantity,
                    availableQuantity,
                    bookItemEntries
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