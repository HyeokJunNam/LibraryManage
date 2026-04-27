package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.util.QuerydslFilterHelper;
import com.nhj.librarymanage.util.QuerydslSortHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
        BooleanExpression isBorrowed = QuerydslFilterHelper.isNull(borrowRecord.returnedAt);

        List<BorrowRecord> query = jpaQueryFactory
                .selectFrom(borrowRecord)
                .innerJoin(borrowRecord.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin()
                .where(eqMemberId, isBorrowed)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowRecord.id.count())
                .from(borrowRecord)
                .innerJoin(borrowRecord.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowRecord.member, member).fetchJoin()
                .where(eqMemberId, isBorrowed);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

}