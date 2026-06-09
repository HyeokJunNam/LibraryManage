package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Notification;
import com.nhj.librarymanage.util.QuerydslFilterHelper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.nhj.librarymanage.domain.entity.QBook.book;
import static com.nhj.librarymanage.domain.entity.QMember.member;
import static com.nhj.librarymanage.domain.entity.QNotification.notification;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Notification> findAllByBookId(Long bookId) {
        BooleanExpression eqBookId = QuerydslFilterHelper.eq(book.id, bookId);

        return jpaQueryFactory
                .selectFrom(notification)
                .join(notification.book, book).fetchJoin()
                .join(notification.member, member).fetchJoin()
                .where(eqBookId)
                .fetch();
    }

    @Override
    public Optional<Notification> findByBookIdAndMemberId(Long bookId, Long memberId) {
        BooleanExpression eqBookId = QuerydslFilterHelper.eq(book.id, bookId);
        BooleanExpression eqMemberId = QuerydslFilterHelper.eq(member.id, memberId);

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(notification)
                        .join(notification.book, book).fetchJoin()
                        .join(notification.member, member).fetchJoin()
                        .where(eqBookId, eqMemberId)
                        .fetchOne()
        );
    }
}
