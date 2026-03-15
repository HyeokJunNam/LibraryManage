package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.entity.BorrowHistoryEntity;
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

import static com.nhj.librarymanage.domain.entity.QBookEntity.bookEntity;
import static com.nhj.librarymanage.domain.entity.QBorrowHistoryEntity.borrowHistoryEntity;
import static com.nhj.librarymanage.domain.entity.QMemberEntity.memberEntity;

@RequiredArgsConstructor
@Repository
public class BorrowHistoryRepositoryImpl implements BorrowHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of(
                    bookEntity.name
            ));

    @Override
    public Page<BorrowHistoryEntity> findAll(BorrowRequest.SearchConditionDto searchConditionDto, Pageable pageable) {
        BooleanExpression onlyBorrowed = searchConditionDto.isOnlyBorrowed() ? QuerydslFilterHelper.istNotNull(bookEntity.borrowHistoryEntity) : null;

        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(borrowHistoryEntity.createdAt, ORDER_COLUMN_MAP, pageable);

        List<BorrowHistoryEntity> query = jpaQueryFactory
                .selectFrom(borrowHistoryEntity)
                .innerJoin(borrowHistoryEntity.bookEntity, bookEntity).fetchJoin()
                .innerJoin(borrowHistoryEntity.memberEntity, memberEntity).fetchJoin()
                .where(onlyBorrowed)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(borrowHistoryEntity.id.count())
                .from(borrowHistoryEntity)
                .innerJoin(borrowHistoryEntity.bookEntity, bookEntity).fetchJoin()
                .innerJoin(borrowHistoryEntity.memberEntity, memberEntity).fetchJoin()
                .where(onlyBorrowed);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

}