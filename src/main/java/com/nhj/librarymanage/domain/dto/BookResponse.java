package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.Book;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class InfoDto {
        private long id;
        private String title;
        private String author;
        private String publisher;
        private int bookCount;
        private int borrowedCount;
        private boolean canBorrow;
        //private long availableCount;

        public static InfoDto toDto(Book book) {
            //boolean canBorrow = bookEntity.getBookCount() - 0 >= 1 ? true : false;

            return InfoDto.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .publisher(book.getPublisher())
                    //.bookCount(bookEntity.getBookCount())
                    .borrowedCount(0)
                    //.canBorrow(canBorrow)
                    .build();
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
