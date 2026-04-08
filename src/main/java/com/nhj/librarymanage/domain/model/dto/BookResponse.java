package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import lombok.*;

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
            String location,
            int bookItemCount,
            int borrowedCount,
            int availableCount,
            int unavailableCount,
            boolean canBorrow,
            List<bookItemDetail> bookItemDetails
    ) {
        public record bookItemDetail(
                Long id,
                BookItemStatus status,
                boolean borrowed,
                LocalDateTime borrowedAt,
                LocalDateTime dueAt,
                LocalDateTime returnedAt
        ) {
        }

        public static Detail from(Book book) {
            int bookItemCount = 0;
            int borrowedCount = 0;
            int availableCount = 0;
            int unavailableCount = 0;

            List<bookItemDetail> bookItemDetails = new ArrayList<>();

            for (BookItem bookItem : book.getBookItems()) {
                bookItemCount++;
                boolean isBorrowed = false;

                if (bookItem.getStatus() != BookItemStatus.AVAILABLE) {
                    unavailableCount++;
                }
                else {
                    if (bookItem.getBorrowRecord() != null) {
                        borrowedCount++;
                        isBorrowed = true;
                    }
                    else {
                        availableCount++;
                    }
                }

                BorrowRecord borrowRecord = bookItem.getBorrowRecord();

                bookItemDetails.add(
                        new bookItemDetail(
                                bookItem.getId(),
                                bookItem.getStatus(),
                                isBorrowed,
                                borrowRecord != null ? borrowRecord.getBorrowedAt() : null,
                                borrowRecord != null ? borrowRecord.getDueAt() : null,
                                borrowRecord != null ? borrowRecord.getReturnedAt() : null
                        )
                );
            }

            return new Detail(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getLocation(),
                    bookItemCount,
                    borrowedCount,
                    availableCount,
                    unavailableCount,
                    availableCount >= 1,
                    bookItemDetails);
        }
    }

    public record Info(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            String location
    ) {
        public static Info from(Book book) {
            return new Info(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getLocation()
            );
        }
    }

}
