package com.nhj.librarymanage.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookItemRequest {

    // 생성 요청
    @AllArgsConstructor
    @Getter
    public static class CreateDto {
        private String status;
        private String location;
    }

}
