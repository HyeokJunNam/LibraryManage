package com.nhj.librarymanage.model.table;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableViewSupport {

    public static List<SearchField> search(SearchField... fields) {
        return List.of(fields);
    }

    public static List<TableColumn> columns(TableColumn... columns) {
        return List.of(columns);
    }
}
