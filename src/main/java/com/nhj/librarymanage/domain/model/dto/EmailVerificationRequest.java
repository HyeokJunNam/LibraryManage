package com.nhj.librarymanage.domain.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailVerificationRequest {

    @AllArgsConstructor
    @Getter
    public static class Send {
        private String email;
    }


    @AllArgsConstructor
    @Getter
    public static class Verify {
        private String email;
        private String code;
    }
}
