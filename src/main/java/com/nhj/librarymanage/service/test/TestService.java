package com.nhj.librarymanage.service.test;

import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TestService {

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void push() {
        eventPublisher.publishEvent(new BorrowRequest.ReturnBook(1L));
    }


}
