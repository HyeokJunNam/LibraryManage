package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.BookEntity;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class InfoDto {
        private long id;
        private String name;
        private String title;
        private String author;
        private String publisher;
        private boolean borrowed;
        //private long availableCount;

        public static InfoDto toDto(BookEntity bookEntity) {
            return InfoDto.builder()
                    .id(bookEntity.getId())
                    .title(bookEntity.getName())
                    .name(bookEntity.getName())
                    .borrowed(bookEntity.isBorrowed())
                    //.availableCount(0)
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
