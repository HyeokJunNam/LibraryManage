package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowHistory;
import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Info {
        private long bookId;
        private String bookTitle;
        private long memberId;
        private String memberName;
        private LocalDateTime borrowedAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;

        public static Info toDto(BorrowHistory borrowHistory) {
            //Book book = borrowHistory.getBook();
            BookItem bookItem = borrowHistory.getBookitem();
            Book book = bookItem.getBook();
            Member member = borrowHistory.getMember();

            return Info.builder()
                    .bookId(bookItem.getBook().getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .borrowedAt(borrowHistory.getBorrowedAt())
                    .dueAt(borrowHistory.getDueAt())
                    .returnedAt(borrowHistory.getReturnedAt())
                    .build();

        }
    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}
