package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookCopyStatus;
import com.nhj.librarymanage.domain.code.BorrowStatus;
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
            EnumOption<BookCopyStatus> bookCopyStatus,
            EnumOption<BorrowStatus> borrowStatus,
            LocalDateTime createdAt
    ) {
        public static Info from(BookCopy bookCopy) {
            return new Info(
                    bookCopy.getId(),
                    bookCopy.getLocation(),
                    EnumOption.from(bookCopy.getStatus()),
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
