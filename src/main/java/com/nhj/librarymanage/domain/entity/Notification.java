package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.domain.code.NotificationChannel;
import com.nhj.librarymanage.domain.code.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "notification")
// 추후 고유 객체를 판단 할 근거에 따라 수정 필요함 (오버 엔지니어링 방지)
@Table(
        name = "notification",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_notification_member_book", columnNames = {"member_id", "book_id"})
        }
)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private LocalDateTime notifiedAt;


    @Builder
    public Notification(Member member, Book book, NotificationChannel channel, NotificationType type) {
        this.member = member;
        this.book = book;
        this.channel = channel;
        this.type = type;
    }

    public void markNotified() {
        this.notifiedAt = LocalDateTime.now();
    }
}
