package com.nhj.librarymanage.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowRequest {

    // 수행 할 작업에 필요한 파라미터 (CRUD 제외)
    @AllArgsConstructor
    @Getter
    public static class SearchDto {

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
