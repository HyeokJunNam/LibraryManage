package com.nhj.librarymanage.domain.model.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowRequest {

    @AllArgsConstructor
    @Getter
    public static class Param {
        private boolean borrowed;
    }

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class SearchCondition {
        private boolean onlyBorrowed;

        public static SearchCondition of(boolean onlyBorrowed) {
            return SearchCondition.builder()
                    .onlyBorrowed(onlyBorrowed)
                    .build();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Borrow {
        private long memberId;
        private List<BorrowBookEntry> borrowBookEntries;
    }

    @AllArgsConstructor
    @Getter
    public static class ReturnBook {
        private List<Long> bookRecordIds;
    }

}
