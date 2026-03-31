package com.nhj.librarymanage.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
