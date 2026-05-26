package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import com.nhj.librarymanage.util.QuerydslFilterHelper;
import com.nhj.librarymanage.util.QuerydslSortHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.nhj.librarymanage.domain.entity.QBook.book;
import static com.nhj.librarymanage.domain.entity.QBookCopy.bookCopy;
import static com.nhj.librarymanage.domain.entity.QBorrowRecord.borrowRecord;
import static com.nhj.librarymanage.domain.entity.QMember.member;

@RequiredArgsConstructor
@Repository
public class BorrowRecordRepositoryImpl implements BorrowRecordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of());


    private JPAQuery<BorrowRecord> searchQuery(Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(borrowRecord)
                .innerJoin(borrowRecord.bookCopy, bookCopy).fetchJoin()
                .innerJoin(bookCopy.book, book).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }

    @Override
    public Page<BorrowRecord> search(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);

        BooleanExpression likeMemberName = QuerydslFilterHelper.like(borrowRecord.member.name, searchCondition.memberName());
        BooleanExpression likeBookTitle = QuerydslFilterHelper.like(book.title, searchCondition.bookTitle());

        List<BorrowRecord> query = searchQuery(pageable)
                .where(likeMemberName, likeBookTitle)
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .where(likeMemberName, likeBookTitle);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchByMemberId(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);

        BooleanExpression eqMemberId = QuerydslFilterHelper.eq(borrowRecord.member.id, memberId);

        BooleanExpression eqBookRecordId = QuerydslFilterHelper.eq(borrowRecord.id, searchCondition.bookRecordId());
        BooleanExpression likeBookTitle = QuerydslFilterHelper.like(book.title, searchCondition.bookTitle());

        List<BorrowRecord> query = searchQuery(pageable)
                .where(eqMemberId, eqBookRecordId, likeBookTitle)
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .where(eqMemberId, eqBookRecordId, likeBookTitle);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchReturnableByMemberId(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);

        BooleanExpression eqMemberId = QuerydslFilterHelper.eq(borrowRecord.member.id, memberId);
        BooleanExpression isNullReturnAt = QuerydslFilterHelper.isNull(borrowRecord.returnedAt);

        BooleanExpression eqBookRecordId = QuerydslFilterHelper.eq(borrowRecord.id, searchCondition.bookRecordId());
        BooleanExpression likeBookTitle = QuerydslFilterHelper.like(book.title, searchCondition.bookTitle());

        List<BorrowRecord> query = searchQuery(pageable)
                .where(eqMemberId, eqBookRecordId, likeBookTitle, isNullReturnAt)
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .where(eqMemberId, eqBookRecordId, likeBookTitle, isNullReturnAt);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchByBookId(Long bookId, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);
        BooleanExpression eqBookId = QuerydslFilterHelper.eq(borrowRecord.bookCopy.book.id, bookId); // 얘때문??

        List<BorrowRecord> query = searchQuery(pageable)
                .where(eqBookId)
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .innerJoin(borrowRecord.bookCopy, bookCopy)
                .innerJoin(bookCopy.book, book)
                .where(eqBookId);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    public BorrowStatistics getBorrowStatistics() {
        LocalDate now = LocalDate.now();

        Long totalBookItemCount = jpaQueryFactory
                .select(bookCopy.id.count())
                .from(bookCopy)
                .fetchOne();

        totalBookItemCount = totalBookItemCount != null ? totalBookItemCount : 0L;

        NumberExpression<Long> currentBorrowCount = countWhen(borrowRecord.returnedAt.isNull());
        NumberExpression<Long> overdueBorrowCount = countWhen(borrowRecord.returnedAt.isNull()
                .and(toDate(borrowRecord.dueAt).before(now)));

        return jpaQueryFactory
                .select(Projections.constructor(
                        BorrowStatistics.class,
                        Expressions.constant(totalBookItemCount),
                        borrowRecord.id.count(),
                        currentBorrowCount,
                        overdueBorrowCount
                ))
                .from(borrowRecord)
                .fetchOne();
    }

    private NumberExpression<Long> countWhen(BooleanExpression condition) {
        return new CaseBuilder()
                .when(condition)
                .then(1L)
                .otherwise(0L)
                .sumLong()
                .coalesce(0L);
    }

    private DateExpression<LocalDate> toDate(Expression<? extends LocalDateTime> localDateTime) {
        return Expressions.dateTemplate(
                LocalDate.class,
                "cast({0} as date)",
                localDateTime
        );
    }

    public Page<BorrowRecord> searchOverdueBorrowRecords(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);

        BooleanExpression notReturned = QuerydslFilterHelper.isNull(borrowRecord.returnedAt);
        BooleanExpression overdue = QuerydslFilterHelper.before(borrowRecord.dueAt, LocalDate.now());

        BooleanExpression likeMemberName = QuerydslFilterHelper.like(borrowRecord.member.name, searchCondition.memberName());
        BooleanExpression likeBookTitle = QuerydslFilterHelper.like(book.title, searchCondition.bookTitle());

        List<BorrowRecord> query = searchQuery(pageable)
                .where(notReturned, overdue, likeMemberName, likeBookTitle)
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .where(notReturned, overdue, likeMemberName, likeBookTitle);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

}