package com.nhj.librarymanage.model.table;

import java.util.List;

import static com.nhj.librarymanage.model.table.SearchField.field;
import static com.nhj.librarymanage.model.table.TableColumn.column;
import static com.nhj.librarymanage.model.table.TableViewSupport.columns;
import static com.nhj.librarymanage.model.table.TableViewSupport.search;

public enum BookTableView implements TableViewSpec {

    TABLE(
            search(
                    field("title", "도서명"),
                    field("isbn", "ISBN"),
                    field("author", "저자"),
                    field("publisher", "출판사")
            ),
            columns(
                    TableColumn.leading(""),
                    column("도서명"),
                    column("ISBN"),
                    column("저자"),
                    column("출판사")
            )
    );

    private final List<SearchField> searchFields;
    private final List<TableColumn> columns;

    BookTableView(
            List<SearchField> searchFields,
            List<TableColumn> columns
    ) {
        this.searchFields = searchFields;
        this.columns = columns;
    }

    @Override
    public List<SearchField> getSearchFields() {
        return searchFields;
    }

    @Override
    public List<TableColumn> getColumns() {
        return columns;
    }
}