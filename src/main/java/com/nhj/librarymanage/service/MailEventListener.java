package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.model.event.BookBorrowableEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class MailEventListener {

    private final EmailSender emailSender;
    private final NotificationDispatchService notificationDispatchService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @EventListener
    public void handle(MailTemplate mailTemplate) {
        emailSender.send(mailTemplate);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @EventListener
    public void sendNotify(BookBorrowableEvent event) {
        notificationDispatchService.dispatchBorrowableNotifications(event.bookId());
    }

}
