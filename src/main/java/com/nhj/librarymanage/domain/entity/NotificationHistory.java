package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.domain.code.NotificationChannel;
import com.nhj.librarymanage.domain.code.NotificationResult;
import com.nhj.librarymanage.domain.code.NotificationType;
import com.nhj.librarymanage.domain.dto.admin.notification.NotificationTemplate;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "notification_history")
public class NotificationHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String memberName;

    private Long bookId;

    private String bookTitle;

    private String isbn;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationResult result;

    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    @Column(length = 1000)
    private String failureReason;

    private NotificationHistory(
            NotificationTemplate notificationTemplate,
            NotificationResult result,
            LocalDateTime processedAt,
            String failureReason
    ) {
        this.memberId = notificationTemplate.memberId();
        this.memberName = notificationTemplate.memberName();
        this.bookId = notificationTemplate.bookId();
        this.bookTitle = notificationTemplate.bookTitle();
        this.isbn = notificationTemplate.isbn();
        this.channel = notificationTemplate.channel();
        this.type = notificationTemplate.type();
        this.requestedAt = notificationTemplate.requestedAt();
        this.result = result;
        this.processedAt = processedAt;
        this.failureReason = failureReason;
    }


    public static NotificationHistory notified(NotificationTemplate notificationTemplate) {
        return new NotificationHistory(
                notificationTemplate,
                NotificationResult.NOTIFIED,
                LocalDateTime.now(),
                null
        );
    }

    public static NotificationHistory canceled(NotificationTemplate notificationTemplate) {
        return new NotificationHistory(
                notificationTemplate,
                NotificationResult.CANCELED,
                LocalDateTime.now(),
                null
        );
    }

    public static NotificationHistory failed(NotificationTemplate notificationTemplate, String failureReason) {
        return new NotificationHistory(
                notificationTemplate,
                NotificationResult.FAILED,
                LocalDateTime.now(),
                failureReason
        );
    }

}
