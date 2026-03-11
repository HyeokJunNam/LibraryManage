package com.nhj.librarymanage.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequest {

    // 수행 할 작업에 필요한 파라미터 (CRUD 제외)
    @AllArgsConstructor
    @Getter
    public static class SearchDto {

    }

    // 생성 요청
    @AllArgsConstructor
    @Getter
    public static class CreateDto {
        private String name;
    }

    // 수정 요청
    @AllArgsConstructor
    @Getter
    public static class UpdateDto {
        private long id;
        private String name;

    }

}
