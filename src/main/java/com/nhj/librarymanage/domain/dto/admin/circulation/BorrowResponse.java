package com.nhj.librarymanage.domain.dto.admin.circulation;

import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BorrowResponse {

    @Builder(access = AccessLevel.PRIVATE)
    public record ListItem(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            Long memberId,
            String loginId,
            String memberName, // 얘
            String memberNo,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt,
            EnumOption<ReturnStatus> returnStatus
    ) {
        public static ListItem from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();
            Member member = borrowRecord.getMember();

            return ListItem.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .loginId(member.getLoginId())
                    .memberName(member.getName())
                    .memberNo(member.getMemberNo())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .returnStatus(EnumOption.from(borrowRecord.getReturnStatus()))
                    .build();
        }
    }


    @Builder(access = AccessLevel.PRIVATE)
    public record OverdueListItem(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            Long memberId,
            String loginId,
            String memberNo,
            String memberName, // 얘
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            long overdueDays
    ) {
        public static OverdueListItem from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();
            Member member = borrowRecord.getMember();

            long overdueDays = ChronoUnit.DAYS.between(borrowRecord.getDueAt().toLocalDate(), LocalDate.now());

            return OverdueListItem.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .loginId(member.getLoginId())
                    .memberNo(member.getMemberNo())
                    .memberName(member.getName())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .overdueDays(overdueDays)
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record ReturnableListItem(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            EnumOption<ReturnStatus> returnStatus
    ) {
        public static ReturnableListItem from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();

            return ReturnableListItem.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnStatus(EnumOption.from(borrowRecord.getReturnStatus()))
                    .build();
        }
    }

}
