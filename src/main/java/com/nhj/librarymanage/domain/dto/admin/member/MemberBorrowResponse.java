package com.nhj.librarymanage.domain.dto.admin.member;

import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;

public class MemberBorrowResponse {

    @Builder(access = AccessLevel.PRIVATE)
    public record ListItem(
            Long borrowRecordId,
            Long bookCopyId,
            String bookTitle,
            LocalDateTime borrowedAt,
            LocalDateTime dueAt,
            LocalDateTime returnedAt,
            EnumOption<ReturnStatus> returnStatus
    ) {
        public static ListItem from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Book book = bookCopy.getBook();

            return ListItem.builder()
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


}
