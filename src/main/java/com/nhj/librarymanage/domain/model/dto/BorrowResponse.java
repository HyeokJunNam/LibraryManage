package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Info {
        private long borrowRecordId;
        private long bookItemId;
        private long bookId;
        private String bookTitle;
        private long memberId;
        private String memberName;
        private LocalDateTime borrowedAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;

        public static Info toDto(BorrowRecord borrowRecord) {
            BookItem bookItem = borrowRecord.getBookitem();
            Book book = bookItem.getBook();
            Member member = borrowRecord.getMember();

            return Info.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookItemId(bookItem.getId())
                    .bookId(book.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .build();

        }
    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}
