package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookItemRequest {

    // 생성 요청
    @AllArgsConstructor
    @Getter
    public static class Create {
        private BookItemStatus status;
        private String location;
    }

}
