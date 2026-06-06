package com.nhj.librarymanage.domain.dto.admin.book;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.entity.BookCopy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookCopyResponse {

    public record ListItem(
            Long bookCopyId,
            String location,
            EnumOption<BookCopyCondition> bookCopyCondition,
            EnumOption<BorrowStatus> borrowStatus,
            LocalDateTime createdAt
    ) {
        public static ListItem from(BookCopy bookCopy) {
            return new ListItem(
                    bookCopy.getId(),
                    bookCopy.getLocation(),
                    EnumOption.from(bookCopy.getBookCopyCondition()),
                    EnumOption.from(bookCopy.getBorrowStatus()),
                    bookCopy.getCreatedAt()
            );
        }
    }

}
