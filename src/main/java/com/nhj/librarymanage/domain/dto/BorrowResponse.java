package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.BookEntity;
import com.nhj.librarymanage.domain.entity.BorrowHistoryEntity;
import com.nhj.librarymanage.domain.entity.MemberEntity;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class InfoDto {
        private long bookId;
        private String bookName;
        private long memberId;
        private String memberName;
        private LocalDateTime borrowedAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;

        public static InfoDto toDto(BorrowHistoryEntity borrowHistoryEntity) {
            BookEntity bookEntity = borrowHistoryEntity.getBookEntity();
            MemberEntity memberEntity = borrowHistoryEntity.getMemberEntity();

            return InfoDto.builder()
                    .bookId(bookEntity.getId())
                    .bookName(bookEntity.getName())
                    .memberId(memberEntity.getId())
                    .memberName(memberEntity.getName())
                    .borrowedAt(borrowHistoryEntity.getBorrowedAt())
                    .dueAt(borrowHistoryEntity.getDueAt())
                    .returnedAt(borrowHistoryEntity.getReturnedAt())
                    .build();

        }
    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}
