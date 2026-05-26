package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.dto.MemberRequest;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import com.nhj.librarymanage.model.vo.MemberStatistics;
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

import static com.nhj.librarymanage.domain.entity.QBorrowRecord.borrowRecord;
import static com.nhj.librarymanage.domain.entity.QMember.member;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of());

    @Override
    public Page<Member> search(MemberRequest.SearchCondition searchCondition, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(member.id, ORDER_COLUMN_MAP, pageable);

        BooleanExpression likeName = QuerydslFilterHelper.like(member.name, searchCondition.name());
        BooleanExpression likePhoneNumber = QuerydslFilterHelper.like(member.phoneNumber, searchCondition.phoneNumber());
        BooleanExpression likeEmail = QuerydslFilterHelper.like(member.email, searchCondition.email());

        List<Member> query = jpaQueryFactory
                .selectFrom(member)
                .where(likeName, likePhoneNumber, likeEmail)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(member.id.count())
                .from(member)
                .where(likeName, likePhoneNumber, likeEmail);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

    public MemberStatistics getMemberStatistics() {
        LocalDate now = LocalDate.now();

        Long totalMemberCount = jpaQueryFactory
                .select(member.id.count())
                .from(member)
                .fetchOne();

        totalMemberCount = totalMemberCount != null ? totalMemberCount : 0L;

        NumberExpression<Long> todayBorrowMemberCount =
                countDistinctMemberWhen(
                        toDate(borrowRecord.borrowedAt).eq(now)
                );

        NumberExpression<Long> currentBorrowMemberCount =
                countDistinctMemberWhen(
                        borrowRecord.returnedAt.isNull()
                );

        NumberExpression<Long> currentOverdueMemberCount =
                countDistinctMemberWhen(
                        borrowRecord.returnedAt.isNull()
                                .and(toDate(borrowRecord.dueAt).before(now))
                );


        return jpaQueryFactory
                .select(Projections.constructor(
                        MemberStatistics.class,
                        Expressions.constant(totalMemberCount),
                        todayBorrowMemberCount,
                        currentBorrowMemberCount,
                        currentOverdueMemberCount
                ))
                .from(borrowRecord)
                .fetchOne();
    }

    private NumberExpression<Long> countDistinctMemberWhen(BooleanExpression condition) {
        return new CaseBuilder()
                .when(condition)
                .then(borrowRecord.member.id)
                .otherwise((Long) null)
                .countDistinct();
    }

    private DateExpression<LocalDate> toDate(Expression<? extends LocalDateTime> localDateTime) {
        return Expressions.dateTemplate(
                LocalDate.class,
                "cast({0} as date)",
                localDateTime
        );
    }
}
