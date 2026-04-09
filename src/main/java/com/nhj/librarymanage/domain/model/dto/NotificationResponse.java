package com.nhj.librarymanage.domain.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

    // TODO 이름 바꾸기
    @Builder(access = AccessLevel.PRIVATE)
    public record Status(
            // TODO 추가하거나 알지?
            boolean requested

    ) {
        public static Status from(boolean requested) {
            return Status.builder()
                    .requested(requested)
                    .build();
        }
    }

}
