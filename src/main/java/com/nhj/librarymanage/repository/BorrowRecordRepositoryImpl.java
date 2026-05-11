package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.dto.BorrowHistoryRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowStatistics;
import com.nhj.librarymanage.util.QuerydslFilterHelper;
import com.nhj.librarymanage.util.QuerydslSortHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
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
    public Page<BorrowRecord> search(BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
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
    public Page<BorrowRecord> searchByMemberId(Long memberId, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);
        BooleanExpression eqMemberId = QuerydslFilterHelper.eq(borrowRecord.member.id, memberId);

        List<BorrowRecord> query = searchQuery(pageable)
                .where(eqMemberId)
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .where(eqMemberId);

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
        LocalDateTime now = LocalDateTime.now();

        Long totalBookItemCount = jpaQueryFactory
                .select(bookCopy.id.count())
                .from(bookCopy)
                .fetchOne();

        totalBookItemCount = totalBookItemCount != null ? totalBookItemCount : 0L;

        return jpaQueryFactory
                .select(Projections.constructor(
                        BorrowStatistics.class,
                        Expressions.constant(totalBookItemCount),
                        // 누적 대출 수
                        borrowRecord.id.count(),
                        // 현재 대출 수
                        new CaseBuilder()
                                .when(borrowRecord.returnedAt.isNull())
                                .then(1L)
                                .otherwise(0L)
                                .sumLong()
                                .coalesce(0L),

                        // 연체 대출 수
                        new CaseBuilder()
                                .when(
                                        borrowRecord.returnedAt.isNull()
                                                .and(borrowRecord.dueAt.before(now))
                                )
                                .then(1L)
                                .otherwise(0L)
                                .sumLong()
                                .coalesce(0L)
                ))
                .from(borrowRecord)
                .fetchOne();
    }

    public Page<BorrowRecord> searchOverdueBorrowRecords(BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
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