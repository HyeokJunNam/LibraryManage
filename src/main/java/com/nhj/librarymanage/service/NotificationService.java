package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.admin.notification.NotificationTemplate;
import com.nhj.librarymanage.domain.dto.temp.NotificationRequest;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.error.code.NotificationErrorCode;
import com.nhj.librarymanage.error.exception.notification.AlreadyRequestedNotificationException;
import com.nhj.librarymanage.repository.BookRepository;
import com.nhj.librarymanage.repository.MemberRepository;
import com.nhj.librarymanage.repository.NotificationRepository;
import com.nhj.librarymanage.security.member.CurrentAuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;

    private final NotificationSender notificationSender;
    private final NotificationHistoryService notificationHistoryService;

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void requestNotify(Long bookId, Long memberId, NotificationRequest.Create create) {
        if (hasNotificationRequest(memberId, bookId)) {
            throw new AlreadyRequestedNotificationException(NotificationErrorCode.NOTIFICATION_ALREADY_REQUESTED);
        }

        Member member = memberRepository.getById(memberId);
        Book book = bookRepository.getById(bookId);

        Notification notification = Notification.builder()
                .member(member)
                .book(book)
                .channel(create.channel())
                .type(create.type())
                .build();

        notificationRepository.save(notification);
    }


    @Transactional
    public void cancelNotify(Long bookId, Long memberId) {
        Notification notification = notificationRepository.getByBookIdAndMemberId(bookId, memberId);
        Book book = bookRepository.getById(bookId);

        notificationHistoryService.canceled(NotificationTemplate.of(book, notification));
        notificationRepository.deleteByBookIdAndMemberId(bookId, memberId);
    }


    @Transactional
    public void send(Long bookId) {
        Book book = bookRepository.getById(bookId);

        List<Notification> notifications = notificationRepository.findAllByBookId(bookId); // TODO 다보내면 안되긴 함

        for (Notification notification : notifications) {
            NotificationTemplate notificationTemplate = NotificationTemplate.of(book, notification);
            notificationSender.send(notificationTemplate);
        }

    }

    public void failSend() {

    }






    public boolean hasNotificationRequest(Long bookId) {
        Long memberId = currentAuthenticatedUserProvider.findCurrentUserId().orElse(null);

        if (memberId != null) {
            return hasNotificationRequest(bookId, memberId);
        }
        else {
            return false;
        }
    }

    public boolean hasNotificationRequest(Long bookId, Long memberId) {
        return notificationRepository.existsByBookIdAndMemberId(bookId, memberId);
    }



}
