package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.util.NumberParser;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowRequest {

    public record SearchCondition(
            boolean onlyBorrowed
    ) {
    }

    public record Create(
            String memberId,
            List<Item> items
    ) {
        public record Item(
                Long bookId,
                long quantity
        ) {
        }

        public Long memberIdAsLong() {
            return NumberParser.parseLong(memberId);
        }
    }

}
