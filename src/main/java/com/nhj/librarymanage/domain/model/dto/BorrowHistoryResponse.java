package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowHistoryResponse {

    @Builder(access = AccessLevel.PRIVATE)
    public record Info(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            String location,
            Long memberId,
            String memberName, // 얘
            EnumOption<BorrowStatus> borrowStatus,
            String borrowStatusLabel,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt
    ) {
        public static Info from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();
            Member member = borrowRecord.getMember();

            EnumOption<BorrowStatus> borrowStatus = EnumOption.from(bookCopy.getBorrowStatus());

            if (borrowStatus.value() == BorrowStatus.AVAILABLE && borrowRecord.getReturnedAt() != null) {
                borrowStatus = EnumOption.from(BorrowStatus.RETURNED);
            }

            return Info.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .bookTitle(book.getTitle())
                    .location(bookCopy.getLocation())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .borrowStatus(borrowStatus)
                    .borrowStatusLabel(borrowStatus.label())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .build();
        }
    }


    @Builder(access = AccessLevel.PRIVATE)
    public record InfoByBook(
            Long borrowRecordId,
            Long bookCopyId,
            Long memberId,
            String memberName, // 얘
            EnumOption<BorrowStatus> borrowStatus,
            String borrowStatusLabel,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt
    ) {
        public static InfoByBook from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Member member = borrowRecord.getMember();

            EnumOption<BorrowStatus> borrowStatus = EnumOption.from(bookCopy.getBorrowStatus());

            if (borrowStatus.value() == BorrowStatus.AVAILABLE && borrowRecord.getReturnedAt() != null) {
                borrowStatus = EnumOption.from(BorrowStatus.RETURNED);
            }

            return InfoByBook.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
                    .memberId(member.getId())
                    .memberName(member.getName())
                    .borrowStatus(borrowStatus)
                    .borrowStatusLabel(borrowStatus.label())
                    .borrowedAt(borrowRecord.getBorrowedAt())
                    .dueAt(borrowRecord.getDueAt())
                    .returnedAt(borrowRecord.getReturnedAt())
                    .build();
        }
    }

    @Builder
    public record InfoByMember(
            Long borrowRecordId,
            Long bookCopyId,
            Long memberId,
            String memberName,
            String bookTitle,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt
    ) {
        public static InfoByMember from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();
            Member member = borrowRecord.getMember();

            return InfoByMember.builder()
                    .borrowRecordId(borrowRecord.getId())
                    .bookCopyId(bookCopy.getId())
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
