package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowRecordResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Summary {
        private long borrowRecordId;
        private long bookItemId;
        private long memberId;
        private String memberName;
        private String bookTitle;
        private LocalDateTime borrowedAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;

        public static Summary from(BorrowRecord borrowRecord) {
            BookItem bookItem = borrowRecord.getBookitem();
            Book book = bookItem.getBook();
            Member member = borrowRecord.getMember();

            return Summary.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookItemId(bookItem.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class BookSummary {
        private long borrowRecordId;
        private long bookItemId;
        private long memberId;
        private String memberName;
        private String bookTitle;
        private LocalDateTime borrowedAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;

        public static BookSummary from(BorrowRecord borrowRecord) {
            BookItem bookItem = borrowRecord.getBookitem();
            Book book = bookItem.getBook();
            Member member = borrowRecord.getMember();

            return BookSummary.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookItemId(bookItem.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class MemberSummary {
        private long borrowRecordId;
        private long bookItemId;
        private long memberId;
        private String memberName;
        private String bookTitle;
        private LocalDateTime borrowedAt;
        private LocalDateTime dueAt;
        private LocalDateTime returnedAt;

        public static MemberSummary from(BorrowRecord borrowRecord) {
            BookItem bookItem = borrowRecord.getBookitem();
            Book book = bookItem.getBook();
            Member member = borrowRecord.getMember();

            return MemberSummary.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookItemId(bookItem.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .build();
        }
    }

}
