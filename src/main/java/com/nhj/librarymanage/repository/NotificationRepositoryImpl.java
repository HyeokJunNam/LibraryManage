package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Notification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nhj.librarymanage.domain.entity.QBook.book;
import static com.nhj.librarymanage.domain.entity.QMember.member;
import static com.nhj.librarymanage.domain.entity.QNotification.notification;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Notification> findAllByBookId(Long bookId) {
        return jpaQueryFactory
                .selectFrom(notification)
                .join(notification.book, book).fetchJoin()
                .join(notification.member, member).fetchJoin()
                .where(book.id.eq(bookId))
                .fetch();
    }
}
