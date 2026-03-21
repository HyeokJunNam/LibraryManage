package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    public record Info(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            long bookCount,
            long borrowedCount,
            boolean canBorrow,
            List<bookItemInfo> bookItemInfos
    ) {
        public record bookItemInfo(
                Long id,
                BookItemStatus status,
                String location
        ) {
        }

        public static Info of(Book book, Map<Long, List<BookItem>> bookItemMap) {
            List<BookItem> bookItemEntities = bookItemMap.getOrDefault(book.getId(), Collections.emptyList());

            long bookCount = 0;
            long borrowedCount = 0;

            List<bookItemInfo> bookItemInfos = new ArrayList<>();

            for (BookItem bookItem : bookItemEntities) {
                bookCount++;

                if (bookItem.getBorrowHistory() != null) {
                    borrowedCount++;
                }

                bookItemInfos.add(
                        new bookItemInfo(
                                bookItem.getId(),
                                bookItem.getStatus(),
                                bookItem.getLocation()
                        )
                );
            }

            return new Info(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    bookCount,
                    borrowedCount,
                    bookCount - borrowedCount >= 1,
                    bookItemInfos);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DetailDto {
        private long id;
        private String name;
        private boolean borrowed;
        private LocalDateTime borrowAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;
    }

}
