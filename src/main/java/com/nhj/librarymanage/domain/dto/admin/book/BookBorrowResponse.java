package com.nhj.librarymanage.domain.dto.admin.book;

import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;

public class BookBorrowResponse {

    @Builder(access = AccessLevel.PRIVATE)
    public record ListItem(
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
        public static ListItem from(BorrowRecord borrowRecord) {
            BookCopy bookCopy = borrowRecord.getBookCopy();
            Member member = borrowRecord.getMember();

            return ListItem.builder()
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


}
