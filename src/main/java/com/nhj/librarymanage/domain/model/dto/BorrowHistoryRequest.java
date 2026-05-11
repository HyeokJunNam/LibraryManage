package com.nhj.librarymanage.domain.model.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowHistoryRequest {

    public record SearchCondition(
            String bookTitle,
            String memberName
    ) {

    }

}
