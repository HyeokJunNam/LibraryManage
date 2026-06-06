package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.code.NotificationChannel;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.model.vo.MailContent;
import com.nhj.librarymanage.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationDispatchService {

    private final NotificationRepository notificationRepository;
    private final EmailSender emailSender;

    private final MailTemplateRenderer mailTemplateRenderer;

    @Transactional
    public void dispatchBorrowableNotifications(Long bookId) {
        List<Notification> notifications = notificationRepository.findAllByBookIdAndNotifiedAtIsNull(bookId);

        for (Notification notification : notifications) {
            if (notification.getChannel() == NotificationChannel.EMAIL) {
                Book book = notification.getBook();

                Context context = new Context();
                context.setVariable("bookTitle", book.getTitle());
                context.setVariable("author", book.getAuthor());
                context.setVariable("requestDate", notification.getCreatedAt().toLocalDate());

                String htmlURL = "mail/book-borrowable.html";
                String textURL = "mail/book-borrowable.txt";

                String toEmail = notification.getMember().getEmail();

                MailContent mailContent = mailTemplateRenderer.renderMailContent(toEmail, htmlURL, textURL, context);

                emailSender.send(mailContent);
            }


            notification.markNotified();
        }
    }

}
