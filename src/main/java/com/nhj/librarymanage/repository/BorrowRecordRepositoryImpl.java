package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.dto.admin.book.BookBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.circulation.BorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.member.info.MyInfoResponse;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.nhj.librarymanage.domain.entity.QBook.book;
import static com.nhj.librarymanage.domain.entity.QBookCopy.bookCopy;
import static com.nhj.librarymanage.domain.entity.QBorrowRecord.borrowRecord;
import static com.nhj.librarymanage.domain.entity.QMember.member;
import static com.nhj.librarymanage.util.QuerydslFilterHelper.*;
import static com.nhj.librarymanage.util.QuerydslSortHelper.*;

@RequiredArgsConstructor
@Repository
public class BorrowRecordRepositoryImpl implements BorrowRecordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            buildOrderColumnMap(List.of());

    private JPAQuery<BorrowRecord> baseSearchQuery() {
        return jpaQueryFactory
                .selectFrom(borrowRecord)
                .innerJoin(borrowRecord.bookCopy, bookCopy).fetchJoin()
                .innerJoin(bookCopy.book, book).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin();
    }

    private JPAQuery<Long> baseCountQuery() {
        return jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .innerJoin(borrowRecord.bookCopy, bookCopy)
                .innerJoin(bookCopy.book, book)
                .innerJoin(borrowRecord.member, member);
    }

    private Page<BorrowRecord> getPage(Pageable pageable, BooleanExpression... conditions) {
        OrderSpecifier<?>[] order = sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);

        List<BorrowRecord> content = baseSearchQuery()
                .where(conditions)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = baseCountQuery()
                .where(conditions);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> search(BorrowRequest.Search search, Pageable pageable) {
        return getPage(
                pageable,
                like(member.name, search.memberName()),
                like(book.title, search.bookTitle())
        );
    }

    @Override
    public Page<BorrowRecord> searchByMemberId(Long memberId, MemberBorrowRequest.Search search, Pageable pageable) {
        return getPage(
                pageable,
                eq(member.id, memberId),
                eq(borrowRecord.id, search.bookRecordId()),
                like(book.title, search.bookTitle())
        );
    }

    @Override
    public Page<BorrowRecord> searchReturnableByMemberId(Long memberId, MemberBorrowRequest.Search search, Pageable pageable) {
        return getPage(
                pageable,
                eq(member.id, memberId),
                isNull(borrowRecord.returnedAt),
                eq(borrowRecord.id, search.bookRecordId()),
                like(book.title, search.bookTitle())
        );
    }

    @Override
    public Page<BorrowRecord> searchByBookId(Long bookId, BookBorrowRequest.Search search, Pageable pageable) {
        return getPage(
                pageable,
                eq(book.id, bookId),
                like(member.name, search.memberName()),
                like(member.memberNo, search.memberNo())
        );
    }

    @Override
    public Page<BorrowRecord> searchOverdue(BorrowRequest.Search search, Pageable pageable) {
        return getPage(
                pageable,
                isNull(borrowRecord.returnedAt),
                before(borrowRecord.dueAt, LocalDate.now()),
                like(member.name, search.memberName()),
                like(book.title, search.bookTitle())
        );
    }

    @Override
    public BorrowStatistics getBorrowStatistics() {
        LocalDate now = LocalDate.now();

        Long totalBookItemCount = jpaQueryFactory
                .select(bookCopy.id.count())
                .from(bookCopy)
                .fetchOne();

        NumberExpression<Long> currentBorrowCount = countWhen(
                borrowRecord.returnedAt.isNull()
        );

        NumberExpression<Long> overdueBorrowCount = countWhen(
                borrowRecord.returnedAt.isNull()
                        .and(toDate(borrowRecord.dueAt).before(now))
        );

        return jpaQueryFactory
                .select(Projections.constructor(
                        BorrowStatistics.class,
                        Expressions.constant(totalBookItemCount != null ? totalBookItemCount : 0L),
                        borrowRecord.id.count(),
                        currentBorrowCount,
                        overdueBorrowCount
                ))
                .from(borrowRecord)
                .fetchOne();
    }

    @Override
    public MyInfoResponse.BorrowStatistics getBorrowStatisticsByMemberId(Long memberId) {
        LocalDate now = LocalDate.now();

        NumberExpression<Long> currentBorrowCount = countWhen(
                borrowRecord.returnedAt.isNull()
        );

        NumberExpression<Long> overdueBorrowCount = countWhen(
                borrowRecord.returnedAt.isNull()
                        .and(toDate(borrowRecord.dueAt).before(now))
        );

        return jpaQueryFactory
                .select(Projections.constructor(
                        MyInfoResponse.BorrowStatistics.class,
                        borrowRecord.id.count(),
                        currentBorrowCount,
                        overdueBorrowCount
                ))
                .from(borrowRecord)
                .where(eq(borrowRecord.member.id, memberId))
                .fetchOne();
    }
}