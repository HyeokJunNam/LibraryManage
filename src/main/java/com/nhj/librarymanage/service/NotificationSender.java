package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.code.NotificationChannel;
import com.nhj.librarymanage.domain.dto.admin.notification.NotificationTemplate;
import com.nhj.librarymanage.model.vo.MailContent;
import com.nhj.librarymanage.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
public class NotificationSender {

    private final EmailSender emailSender;
    private final MailTemplateRenderer mailTemplateRenderer;

    private final NotificationRepository notificationRepository;
    private final NotificationHistoryService notificationHistoryService;

    @Transactional
    @Async("mailTaskExecutor")
    public void send(NotificationTemplate notificationTemplate) {
        try {
            if (notificationTemplate.channel() == NotificationChannel.EMAIL) {

                Context context = new Context();
                context.setVariable("bookTitle", notificationTemplate.bookTitle());
                context.setVariable("author", notificationTemplate.author());
                context.setVariable("requestDate", notificationTemplate.requestedAt().toLocalDate());

                String htmlURL = "mail/book-borrowable.html";
                String textURL = "mail/book-borrowable.txt";

                String toEmail = notificationTemplate.toEmail();

                MailContent mailContent = mailTemplateRenderer.renderMailContent(toEmail, htmlURL, textURL, context);

                emailSender.send(mailContent);

                notificationHistoryService.notified(notificationTemplate);
                notificationRepository.deleteByBookIdAndMemberId(notificationTemplate.bookId(), notificationTemplate.memberId());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

}
