package com.nhj.librarymanage.domain.dto.admin.notification;

import com.nhj.librarymanage.domain.code.NotificationChannel;
import com.nhj.librarymanage.domain.code.NotificationType;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.entity.Notification;

import java.time.LocalDateTime;

public record NotificationTemplate(
        // member
        Long memberId,
        String memberName,

        // book
        Long bookId,
        String bookTitle,
        String isbn,
        String author,

        // notification
        NotificationChannel channel,
        NotificationType type,
        LocalDateTime requestedAt,


        String toEmail
) {
    public static NotificationTemplate of(Book book, Notification notification) {
        Member member = notification.getMember();

        return new NotificationTemplate(
                member.getId(),
                member.getName(),
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAuthor(),
                notification.getChannel(),
                notification.getType(),
                notification.getCreatedAt(),
                notification.getMember().getEmail()
        );
    }
}
