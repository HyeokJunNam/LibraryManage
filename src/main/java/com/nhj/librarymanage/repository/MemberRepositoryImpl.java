package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.model.dto.MemberRequest;
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

import static com.nhj.librarymanage.domain.entity.QMember.member;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Map<String, Expression<? extends Comparable<?>>> ORDER_COLUMN_MAP =
            QuerydslSortHelper.buildOrderColumnMap(List.of());

    @Override
    public Page<Member> findAll(MemberRequest.SearchCondition searchCondition, Pageable pageable) {
        OrderSpecifier<?>[] order = QuerydslSortHelper.sort(member.id, ORDER_COLUMN_MAP, pageable);

        BooleanExpression likeName = QuerydslFilterHelper.like(member.name, searchCondition.getName());
        BooleanExpression likeMemberNo = QuerydslFilterHelper.like(member.memberNo, searchCondition.getMemberNo());
        BooleanExpression likeEmail = QuerydslFilterHelper.like(member.email, searchCondition.getEmail());

        List<Member> query = jpaQueryFactory
                .selectFrom(member)
                .where(likeName, likeMemberNo, likeEmail)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(member.id.count())
                .from(member)
                .where(likeName, likeMemberNo, likeEmail);

        return PageableExecutionUtils.getPage(query, pageable, countQuery::fetchOne);
    }

}
