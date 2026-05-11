package com.nhj.librarymanage.domain.model.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowRequest {

    public record SearchCondition(
            boolean onlyBorrowed
    ) {
    }

    public record Create(
            long memberId,
            List<Item> items
    ) {
        public record Item(
                long bookId,
                long quantity
        ) {
        }

    }

}
