package com.nhj.librarymanage.domain.dto.sample;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleApi {

    // REST API 요청
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class SendDto {

    }

    // REST API 응답
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class ReceiveDto {

    }

}