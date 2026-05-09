package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.EnumOption;
import com.nhj.librarymanage.domain.entity.BookItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookItemResponse {

    public record Attribute(
            int stockQuantity,
            int borrowedQuantity,
            int availableQuantity
    ) {
    }

    public record Summary(
            Long bookItemId,
            String location,
            EnumOption<BookItemStatus> bookItemStatus,
            EnumOption<BorrowStatus> borrowStatus,
            LocalDateTime createdAt
    ) {
        public static Summary from(BookItem bookItem) {
            return new Summary(
                    bookItem.getId(),
                    bookItem.getLocation(),
                    EnumOption.from(bookItem.getStatus()),
                    EnumOption.from(bookItem.getBorrowStatus()),
                    bookItem.getCreatedAt()
            );
        }
    }

}
