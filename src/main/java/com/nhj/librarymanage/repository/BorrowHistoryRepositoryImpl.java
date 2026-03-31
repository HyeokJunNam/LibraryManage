package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.domain.entity.BorrowHistory;
import com.nhj.librarymanage.util.QuerydslSortHelper;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
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
import static com.nhj.librarymanage.domain.entity.QBorrowHistory.borrowHistory;
import static com.nhj.librarymanage.domain.entity.QMember.member;

@RequiredArgsConstructor
@Repository
public class BorrowHistoryRepositoryImpl implements BorrowHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of());

    @Override
    public Page<BorrowHistory> findAll(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        //BooleanExpression onlyBorrowed = searchConditionDto.isOnlyBorrowed() ? QuerydslFilterHelper.istNotNull(book.borrowHistory) : null;

        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowHistory.createdAt, ORDER_COLUMN_MAP, pageable);

        List<BorrowHistory> query = jpaQueryFactory
                .selectFrom(borrowHistory)
                .innerJoin(borrowHistory.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowHistory.member, member).fetchJoin()
                //.where(onlyBorrowed)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowHistory.id.count())
                .from(borrowHistory)
                .innerJoin(borrowHistory.bookitem, bookItem).fetchJoin()
                .innerJoin(borrowHistory.member, member).fetchJoin();
                //.where(onlyBorrowed);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

}