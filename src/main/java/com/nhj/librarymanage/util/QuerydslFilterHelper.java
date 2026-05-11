package com.nhj.librarymanage.util;

import com.querydsl.core.types.dsl.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public static <T> BooleanExpression in(SimpleExpression<T> targetExpression, List<T> searchTerms) {
        return targetExpression.in(searchTerms);
    }

    /**
     * 날짜 범위 검색
     * startDate <= target < endDate + 1일
     */
    public static BooleanExpression range(DateTimePath<LocalDateTime> targetExpression, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        if (startDate != null && endDate != null) {
            return targetExpression.goe(startDate.atStartOfDay())
                    .and(targetExpression.lt(endDate.plusDays(1).atStartOfDay()));
        }

        if (startDate != null) {
            return targetExpression.goe(startDate.atStartOfDay());
        }

        return targetExpression.lt(endDate.plusDays(1).atStartOfDay());
    }

    /**
     * target 날짜가 기준 날짜보다 빠른 경우
     * target < baseDate
     */
    public static BooleanExpression before(DateTimePath<LocalDateTime> targetExpression, LocalDateTime baseDateTime) {
        if (baseDateTime == null) {
            return null;
        }

        return targetExpression.lt(baseDateTime);
    }

    public static BooleanExpression before(DateTimePath<LocalDateTime> targetExpression, LocalDate baseDate) {
        if (baseDate == null) {
            return null;
        }

        return targetExpression.lt(baseDate.atStartOfDay());
    }

    /**
     * target 날짜가 기준 날짜보다 같거나 빠른 경우
     * target <= baseDate
     */
    public static BooleanExpression loe(DateTimePath<LocalDateTime> targetExpression, LocalDateTime baseDateTime) {
        if (baseDateTime == null) {
            return null;
        }

        return targetExpression.loe(baseDateTime);
    }

    public static BooleanExpression loe(DateTimePath<LocalDateTime> targetExpression, LocalDate baseDate) {
        if (baseDate == null) {
            return null;
        }

        return targetExpression.loe(baseDate.atStartOfDay());
    }

    /**
     * target 날짜가 기준 날짜보다 늦은 경우
     * target > baseDate
     */
    public static BooleanExpression after(DateTimePath<LocalDateTime> targetExpression, LocalDateTime baseDateTime) {
        if (baseDateTime == null) {
            return null;
        }

        return targetExpression.gt(baseDateTime);
    }

    public static BooleanExpression after(DateTimePath<LocalDateTime> targetExpression, LocalDate baseDate) {
        if (baseDate == null) {
            return null;
        }

        return targetExpression.gt(baseDate.atStartOfDay());
    }

    /**
     * target 날짜가 기준 날짜보다 같거나 늦은 경우
     * target >= baseDate
     */
    public static BooleanExpression goe(DateTimePath<LocalDateTime> targetExpression, LocalDateTime baseDateTime) {
        if (baseDateTime == null) {
            return null;
        }

        return targetExpression.goe(baseDateTime);
    }

    public static BooleanExpression goe(DateTimePath<LocalDateTime> targetExpression, LocalDate baseDate) {
        if (baseDate == null) {
            return null;
        }

        return targetExpression.goe(baseDate.atStartOfDay());
    }


}