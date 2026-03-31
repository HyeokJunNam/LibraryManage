package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class MailEventListener {

    private final MailSendHelper mailSendHelper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @EventListener
    public void handle(MailTemplate mailTemplate) {
        mailSendHelper.send(mailTemplate);
    }

}
