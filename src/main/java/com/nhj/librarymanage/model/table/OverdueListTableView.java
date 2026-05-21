package com.nhj.librarymanage.model.table;

import java.util.List;

import static com.nhj.librarymanage.model.table.SearchField.field;
import static com.nhj.librarymanage.model.table.TableColumn.column;
import static com.nhj.librarymanage.model.table.TableViewSupport.columns;
import static com.nhj.librarymanage.model.table.TableViewSupport.search;

public enum OverdueListTableView implements TableViewSpec {

    TABLE(
            search(
                    field("bookTitle", "도서명"),
                    field("memberName", "회원명")
            ),
            columns(
                    TableColumn.leading("")
            )
    );

    private final List<SearchField> searchFields;
    private final List<TableColumn> columns;

    OverdueListTableView(
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