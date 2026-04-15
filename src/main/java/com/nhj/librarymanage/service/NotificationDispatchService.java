package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.code.NotificationChannel;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.domain.model.vo.MailContent;
import com.nhj.librarymanage.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationDispatchService {

    private final NotificationRepository notificationRepository;
    private final EmailSender emailSender;

    private final MailTemplateRenderer mailTemplateRenderer;
    private final TemplateEngine templateEngine;

    @Transactional
    public void dispatchBorrowableNotifications(Long bookId) {
        List<Notification> notifications = notificationRepository.findAllByBookId(bookId);

        for (Notification notification : notifications) {
            if (notification.getChannel() == NotificationChannel.EMAIL) {
                Book book = notification.getBook();

                Context context = new Context();
                context.setVariable("bookTitle", book.getTitle());
                context.setVariable("author", book.getAuthor());
                context.setVariable("requestDate", notification.getCreatedAt());

                String html = templateEngine.process("mail/book-available", context);
                String text = templateEngine.process("mail/book-available.txt", context);

                String toEmail = notification.getMember().getEmail();

                MailContent mailContent = mailTemplateRenderer.renderMailContent(toEmail, html, text, context);

                emailSender.send(mailContent);
            }


            notification.markNotified();
        }
    }

}
