package com.nhj.librarymanage.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuerydslFilterHelper {

    public static BooleanExpression like(StringExpression targetExpression, String searchTerm) {
        return StringUtils.hasText(searchTerm) ? targetExpression.like("%" + searchTerm + "%") : null;
    }

    public static BooleanExpression eq(StringExpression targetExpression, String searchTerm) {
        return StringUtils.hasText(searchTerm) ? targetExpression.eq(searchTerm) : null;
    }

    public static <T extends Number & Comparable<?>> BooleanExpression eq(NumberExpression<T> targetExpression, T searchTerm) {
        return searchTerm != null ? targetExpression.eq(searchTerm) : null;
    }

    public static BooleanExpression eq(BooleanExpression targetExpression, Boolean searchTerm) {
        return searchTerm != null ? targetExpression.eq(searchTerm) : null;
    }

    public static BooleanExpression notEq(StringExpression targetExpression, String searchTerm) {
        return StringUtils.hasText(searchTerm) ? targetExpression.ne(searchTerm) : null;
    }

    public static BooleanExpression isNull(SimpleExpression<?> targetExpression) {
        return targetExpression.isNull();
    }

    public static BooleanExpression istNotNull(SimpleExpression<?> targetExpression) {
        return targetExpression.isNotNull();
    }


}