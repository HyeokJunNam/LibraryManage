package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.domain.model.dto.NotificationRequest;
import com.nhj.librarymanage.domain.model.dto.NotificationResponse;
import com.nhj.librarymanage.error.code.NotificationErrorCode;
import com.nhj.librarymanage.error.exception.notification.AlreadyRequestedNotificationException;
import com.nhj.librarymanage.repository.BookRepository;
import com.nhj.librarymanage.repository.MemberRepository;
import com.nhj.librarymanage.repository.NotificationRepository;
import com.nhj.librarymanage.security.member.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationService {

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
            requested = notificationRepository.existsByMemberIdAndBookId(memberId, bookId);
        }
        else{
            requested = false;
        }

        return requested;
    }

    @Transactional
    public void createNotify(Long bookId, Long memberId, NotificationRequest.Create create) {
        if (hasRequested(memberId, bookId)) {
            throw new AlreadyRequestedNotificationException(NotificationErrorCode.NOTIFICATION_ALREADY_REQUESTED);
        }


        Member member = memberRepository.getById(memberId); // 이걸로 하면 안될거 같은데? 요청한 사용자 없음 (X) / 유효하게 인증된 사용자가 없음 (O) 인데 (강제 로그아웃 등으로)
        Book book = bookRepository.getById(bookId);

        Notification notification = Notification.builder()
                .member(member)
                .book(book)
                .channel(create.channel())
                .type(create.type())
                .build();

        notificationRepository.save(notification);
    }

    // 발송 등록 요청

    // 실제 발송인데..  그러니까 Borrowable 을 type으로 바꾸라고? 공용으로 쓸 수 있게끔? 그리고 팩토리로 바꾸고? 하하하 좋은데?
    public void sendNotify(Long bookId) {



    }


}
