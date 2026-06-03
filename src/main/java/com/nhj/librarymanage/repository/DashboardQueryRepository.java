package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.model.vo.DashboardStatistics;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

import static com.nhj.librarymanage.util.QuerydslFilterHelper.*;

import static com.nhj.librarymanage.domain.entity.QBorrowRecord.borrowRecord;

@RequiredArgsConstructor
@Repository
public class DashboardQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public DashboardStatistics getBorrowStatistics() {
        LocalDate now = LocalDate.now();

        NumberExpression<Long> currentBorrowCount = countWhen(
                borrowRecord.returnedAt.isNull()
        );

        NumberExpression<Long> currentOverdueCount = countWhen(
                borrowRecord.returnedAt.isNull()
                        .and(toDate(borrowRecord.dueAt).before(now))
        );

        NumberExpression<Long> currentBorrowMemberCount = countDistinctMemberWhen(
                borrowRecord.returnedAt.isNull()
        );
        NumberExpression<Long> currentOverdueMemberCount = countDistinctMemberWhen(
                borrowRecord.returnedAt.isNull()
                        .and(toDate(borrowRecord.dueAt).before(now))
        );

        NumberExpression<Long> todayBorrowCount = countWhen(
                toDate(borrowRecord.borrowedAt).eq(now)
        );

        NumberExpression<Long> todayReturnDueCount = countWhen(
                borrowRecord.returnedAt.isNull()
                        .and(toDate(borrowRecord.dueAt).eq(now))
        );

        NumberExpression<Long> renewalRequestCount = Expressions.asNumber(0L);

        return jpaQueryFactory
                .select(Projections.constructor(
                        DashboardStatistics.class,
                        currentBorrowCount,
                        currentOverdueCount,
                        currentBorrowMemberCount,
                        currentOverdueMemberCount,
                        todayBorrowCount,
                        todayReturnDueCount,
                        renewalRequestCount

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

}
