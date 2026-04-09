package com.nhj.librarymanage.domain.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailVerificationResponse {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Verified {
        private String token;

        public static Verified from(String token) {
            return new Verified(token);
        }
    }

}
