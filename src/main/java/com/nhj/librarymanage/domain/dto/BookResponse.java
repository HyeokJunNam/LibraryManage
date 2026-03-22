package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowHistory;
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
                String location,
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
                    if (bookItem.getBorrowHistory() != null) {
                        borrowedCount++;
                        isBorrowed = true;
                    }
                    else {
                        availableCount++;
                    }
                }

                BorrowHistory borrowHistory = bookItem.getBorrowHistory();

                bookItemDetails.add(
                        new bookItemDetail(
                                bookItem.getId(),
                                bookItem.getStatus(),
                                bookItem.getLocation(),
                                isBorrowed,
                                borrowHistory != null ? borrowHistory.getBorrowedAt() : null,
                                borrowHistory != null ? borrowHistory.getDueAt() : null,
                                borrowHistory != null ? borrowHistory.getReturnedAt() : null
                        )
                );
            }

            return new Detail(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
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
            String publisher
    ) {
        public static Info from(Book book) {
            return new Info(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher()
            );
        }
    }

}
