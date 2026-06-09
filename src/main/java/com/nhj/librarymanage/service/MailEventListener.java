package com.nhj.librarymanage.service;

import com.nhj.librarymanage.model.event.BookBorrowableEvent;
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
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async // 아 이거 있어서 돌아가나..? 느렸던거 같은데
    public void handle(MailTemplate mailTemplate) {
        emailSender.send(mailTemplate);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    /*@Async*/
    public void sendNotify(BookBorrowableEvent event) {
        notificationService.send(event.bookId());
    }

}
