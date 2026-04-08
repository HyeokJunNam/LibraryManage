package com.nhj.librarymanage.domain.model.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookRequest {

    // 수행 할 작업에 필요한 파라미터 (CRUD 제외)
    @AllArgsConstructor
    @Getter
    public static class Param {

    }

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class SearchCondition {
        private String isbn;
        private String title;
        private String author;
        private String publisher;
    }

    // 생성 요청
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Create {
        private String isbn;
        private String title;
        private String author;
        private String publisher;
        private String location;
    }

    // 수정 요청
    @AllArgsConstructor
    @Getter
    public static class Update {
        private long id;
        private String name;

    }

}
