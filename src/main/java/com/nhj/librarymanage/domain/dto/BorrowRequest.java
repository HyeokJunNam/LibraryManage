package com.nhj.librarymanage.domain.dto;

import lombok.*;

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
        private long bookItemId;
        private long memberId;
    }

    @AllArgsConstructor
    @Getter
    public static class ReturnBook {
        private long bookId;
        // 관리자 ID가 들어갈 수도 있겠구나

    }

}
