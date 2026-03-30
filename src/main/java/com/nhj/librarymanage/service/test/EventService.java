package com.nhj.librarymanage.service.test;

import com.nhj.librarymanage.domain.dto.BorrowRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@Component
public class EventService {

    //@Async("mailExecutor")
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @EventListener
    public void handle(BorrowRequest.Param param) {
        log.info("test!!!!!");
    }


}
