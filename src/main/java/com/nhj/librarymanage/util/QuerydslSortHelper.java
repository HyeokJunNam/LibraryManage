package com.nhj.librarymanage.util;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuerydslSortHelper {

    public static Map<String, Expression<? extends Comparable<?>>> buildOrderColumnMap(List<Path<? extends Comparable<?>>> columnList) {
        Map<String, Expression<? extends Comparable<?>>> map = new HashMap<>();

        for (Path<? extends Comparable<?>> path : columnList) {
            map.put(path.getMetadata().getName(), path);
        }
        return map;
    }

    public static OrderSpecifier<?>[] sort(ComparableExpressionBase<?> defaultOrderColumn, Map<String, Expression<? extends Comparable<?>>> orderColumnMap, Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            Expression<? extends Comparable<?>> orderColumn = orderColumnMap.get(order.getProperty());

            if (orderColumn != null) {
                orderSpecifierList.add(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, orderColumn));
            }
        }

        if (orderSpecifierList.isEmpty()) {
            orderSpecifierList.add(defaultOrderColumn.desc());
        }

        return orderSpecifierList.toArray(new OrderSpecifier[0]);
    }

}