package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.nhj.librarymanage.domain.entity.QBook.book;
import static com.nhj.librarymanage.domain.entity.QBookItem.bookItem;
import static com.nhj.librarymanage.domain.entity.QBorrowRecord.borrowRecord;
import static com.nhj.librarymanage.domain.entity.QMember.member;

@RequiredArgsConstructor
@Repository
public class BorrowRecordRepositoryImpl implements BorrowRecordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of());

    @Override
    public Page<BorrowRecord> search(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        //BooleanExpression onlyBorrowed = searchConditionDto.isOnlyBorrowed() ? QuerydslFilterHelper.istNotNull(book.borrowRecord) : null;

        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);

        List<BorrowRecord> query = jpaQueryFactory
                .selectFrom(borrowRecord)
                .innerJoin(borrowRecord.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin()
                //.where(onlyBorrowed)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .innerJoin(borrowRecord.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin();
                //.where(onlyBorrowed);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchByMemberId(Long memberId, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);
        BooleanExpression eqMemberId = QuerydslFilterHelper.eq(borrowRecord.member.id, memberId);
        BooleanExpression notReturned = QuerydslFilterHelper.isNull(borrowRecord.returnedAt);

        List<BorrowRecord> query = jpaQueryFactory
                .selectFrom(borrowRecord)
                .innerJoin(borrowRecord.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin()
                .where(eqMemberId, notReturned)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .innerJoin(borrowRecord.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin()
                .where(eqMemberId, notReturned);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BorrowRecord> searchByBookId(Long bookId, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowRecord.createdAt, ORDER_COLUMN_MAP, pageable);
        BooleanExpression eqBookId = QuerydslFilterHelper.eq(borrowRecord.bookitem.book.id, bookId);

        List<BorrowRecord> query = jpaQueryFactory
                .selectFrom(borrowRecord)
                .where(eqBookId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .where(eqBookId);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    // 구할 것
    // 총 도서 수 / 대출 가능 도서 수 / 대출 중 도서 수 / 연체 중 도서 수
    // 하지만 "재고" 기준으로 따져야 하긴 함.
    public BorrowStatistics getBorrowStatistics() {
        LocalDateTime now = LocalDateTime.now();

        Long totalBookItemCount = jpaQueryFactory
                .select(bookItem.id.count())
                .from(bookItem)
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

}