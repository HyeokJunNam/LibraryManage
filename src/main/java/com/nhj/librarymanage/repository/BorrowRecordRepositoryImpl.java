package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.dto.admin.book.BookBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
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
import java.util.Arrays;
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


    private List<BorrowRecord> searchQuery(Pageable pageable, BooleanExpression... booleanExpressions) {
        OrderSpecifier<?>[] order = sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);

        return jpaQueryFactory
                .selectFrom(borrowRecord)
                .innerJoin(borrowRecord.bookCopy, bookCopy).fetchJoin()
                .innerJoin(bookCopy.book, book).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin()
                .where(booleanExpressions)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private JPAQuery<Long> searchCountQuery(BooleanExpression... booleanExpressions) {
        return jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .where(booleanExpressions);
    }

    @Override
    public Page<BorrowRecord> search(BorrowRequest.Search search, Pageable pageable) {
        BooleanExpression likeMemberName = like(borrowRecord.member.name, search.memberName());
        BooleanExpression likeBookTitle = like(book.title, search.bookTitle());

        BooleanExpression[] booleanExpressions = {
                likeMemberName, likeBookTitle
        };

        List<BorrowRecord> query = searchQuery(pageable, booleanExpressions);
        JPAQuery<Long> countQuery = searchCountQuery(booleanExpressions);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchByMemberId(Long memberId, MemberBorrowRequest.Search search, Pageable pageable) {
        BooleanExpression eqMemberId = eq(borrowRecord.member.id, memberId);
        BooleanExpression eqBookRecordId = eq(borrowRecord.id, search.bookRecordId());
        BooleanExpression likeBookTitle = like(book.title, search.bookTitle());

        BooleanExpression[] booleanExpressions = {
                eqMemberId, eqBookRecordId, likeBookTitle
        };

        List<BorrowRecord> query = searchQuery(pageable, booleanExpressions);
        JPAQuery<Long> countQuery = searchCountQuery(booleanExpressions);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchReturnableByMemberId(Long memberId, MemberBorrowRequest.Search search, Pageable pageable) {
        BooleanExpression eqMemberId = eq(borrowRecord.member.id, memberId);
        BooleanExpression likeMemberName = isNull(borrowRecord.returnedAt);
        BooleanExpression eqBookRecordId = eq(borrowRecord.id, search.bookRecordId());
        BooleanExpression likeBookTitle = like(book.title, search.bookTitle());

        BooleanExpression[] booleanExpressions = {
                eqMemberId, likeMemberName, eqBookRecordId, likeBookTitle
        };

        List<BorrowRecord> query = searchQuery(pageable, booleanExpressions);
        JPAQuery<Long> countQuery = searchCountQuery(booleanExpressions);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchByBookId(Long bookId, BookBorrowRequest.Search search, Pageable pageable) {
        BooleanExpression eqBookId = eq(borrowRecord.bookCopy.book.id, bookId); // 얘때문??
        BooleanExpression likeMemberName = like(member.name, search.memberName());
        BooleanExpression likeMemberNo = like(member.memberNo, search.memberNo());

        BooleanExpression[] booleanExpressions = {
                eqBookId, likeMemberName, likeMemberNo
        };

        List<BorrowRecord> query = searchQuery(pageable, booleanExpressions);
        JPAQuery<Long> countQuery = searchCountQuery(booleanExpressions);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    public Page<BorrowRecord> searchOverdue(BorrowRequest.Search search, Pageable pageable) {
        BooleanExpression notReturned = isNull(borrowRecord.returnedAt);
        BooleanExpression overdue = before(borrowRecord.dueAt, LocalDate.now());
        BooleanExpression likeMemberName = like(borrowRecord.member.name, search.memberName());
        BooleanExpression likeBookTitle = like(book.title, search.bookTitle());

        BooleanExpression[] booleanExpressions = {
                notReturned, overdue, likeMemberName, likeBookTitle
        };

        List<BorrowRecord> query = searchQuery(pageable, booleanExpressions);
        JPAQuery<Long> countQuery = searchCountQuery(booleanExpressions);

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

}