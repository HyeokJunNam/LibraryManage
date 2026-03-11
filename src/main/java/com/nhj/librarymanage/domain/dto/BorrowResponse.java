package com.nhj.librarymanage.domain.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class InfoDto {


    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}
