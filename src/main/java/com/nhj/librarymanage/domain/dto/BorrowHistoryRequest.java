package com.nhj.librarymanage.domain.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowHistoryRequest {

    public record SearchCondition(
            String bookTitle,
            String memberName
    ) {

    }

    public record SearchConditionByMember(
            String bookTitle,
            Long bookRecordId
    ) {
    }

}
