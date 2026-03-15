package com.nhj.librarymanage.domain.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowRequest {

    @AllArgsConstructor
    @Getter
    public static class ParamDto {
        private boolean borrowed;
    }

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class SearchConditionDto {
        private boolean onlyBorrowed;

        public static SearchConditionDto of(boolean onlyBorrowed) {
            return SearchConditionDto.builder()
                    .onlyBorrowed(onlyBorrowed)
                    .build();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class BorrowDto {
        private long bookId;
        private long memberId;
    }

    @AllArgsConstructor
    @Getter
    public static class ReturnBookDto {
        private long bookId;
        // 관리자 ID가 들어갈 수도 있겠구나

    }

}
