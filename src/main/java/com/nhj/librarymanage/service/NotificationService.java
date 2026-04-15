package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.domain.model.dto.NotificationRequest;
import com.nhj.librarymanage.error.code.NotificationErrorCode;
import com.nhj.librarymanage.error.exception.notification.AlreadyRequestedNotificationException;
import com.nhj.librarymanage.repository.BookRepository;
import com.nhj.librarymanage.repository.MemberRepository;
import com.nhj.librarymanage.repository.NotificationRepository;
import com.nhj.librarymanage.security.member.CurrentAuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final NotificationRepository notificationRepository;

    private void sendEmail() {

    }

    private void sendPush() {

    }

    // TODO 이름이랑 내용 바꾸자
    public boolean hasRequested(Long memberId, Long bookId) {
        boolean requested;

        if (memberId != null) {
            requested = notificationRepository.existsByBookIdAndMemberId(bookId, memberId);
        }
        else{
            requested = false;
        }

        return requested;
    }

    // 실제 발송인데..  그러니까 Borrowable 을 type으로 바꾸라고? 공용으로 쓸 수 있게끔? 그리고 팩토리로 바꾸고? 하하하 좋은데?
    // 근데 이건 이벤트잖음?
    public void sendNotify() {



    }


    @Transactional
    public void createNotify(Long bookId, NotificationRequest.Create create) {
        Long memberId = currentAuthenticatedUserProvider.getCurrentUserId();

        if (hasRequested(memberId, bookId)) {
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
    public void deleteNotify(Long bookId) {
        Long memberId = currentAuthenticatedUserProvider.getCurrentUserId();

        notificationRepository.deleteByBookIdAndMemberId(bookId, memberId);
    }

    // 발송 등록 요청




}
