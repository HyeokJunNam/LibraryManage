package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowResponse {

    @Builder(access = AccessLevel.PRIVATE)
    public record History(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            Long memberId,
            String memberName, // 얘
            String memberNo,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt,
            EnumOption<ReturnStatus> returnStatus
    ) {
        public static History from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();
            Member member = borrowRecord.getMember();

            return History.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
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
    public record BookHistory(
            Long borrowRecordId,
            Long bookCopyId,
            Long memberId,
            String memberName, // 얘
            String memberNo,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt,
            EnumOption<ReturnStatus> returnStatus
    ) {
        public static BookHistory from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Member member = borrowRecord.getMember();

            return BookHistory.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .memberId(member.getId())
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
    public record MemberHistory(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt,
            EnumOption<ReturnStatus> returnStatus
    ) {
        public static MemberHistory from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();

            return MemberHistory.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .returnStatus(EnumOption.from(borrowRecord.getReturnStatus()))
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record Returnable(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            EnumOption<ReturnStatus> returnStatus
    ) {
        public static Returnable from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();

            return Returnable.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnStatus(EnumOption.from(borrowRecord.getReturnStatus()))
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record OverdueHistory(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            Long memberId,
            String memberNo,
            String memberName, // 얘
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            long overdueDays
    ) {
        public static OverdueHistory from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();
            Member member = borrowRecord.getMember();

            long overdueDays = ChronoUnit.DAYS.between(borrowRecord.getDueAt().toLocalDate(), LocalDate.now());

            return OverdueHistory.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .memberId(member.getId())
                    .memberNo(member.getMemberNo())
                    .memberName(member.getName())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .overdueDays(overdueDays)
                    .build();
        }
    }

}
