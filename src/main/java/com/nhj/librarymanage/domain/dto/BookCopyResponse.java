package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.entity.BookCopy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookCopyResponse {

    public record Info(
            Long bookCopyId,
            String location,
            EnumOption<BookCopyCondition> bookCopyCondition,
            EnumOption<BorrowStatus> borrowStatus,
            LocalDateTime createdAt
    ) {
        public static Info from(BookCopy bookCopy) {
            return new Info(
                    bookCopy.getId(),
                    bookCopy.getLocation(),
                    EnumOption.from(bookCopy.getBookCopyCondition()),
                    EnumOption.from(bookCopy.getBorrowStatus()),
                    bookCopy.getCreatedAt()
            );
        }
    }

    public record Quantity(
            int stockQuantity,
            int borrowedQuantity,
            int availableQuantity
    ) {
    }

}
