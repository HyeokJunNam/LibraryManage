package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.NotificationChannel;
import com.nhj.librarymanage.domain.code.NotificationType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationRequest {

    public record Create(
            NotificationChannel channel,
            NotificationType type
    ) {
    }

    public record Delete(
            NotificationChannel channel,
            NotificationType type
    ) {
    }

}
