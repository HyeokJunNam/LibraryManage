package com.nhj.librarymanage.model.table;

import java.util.List;

import static com.nhj.librarymanage.model.table.SearchField.field;
import static com.nhj.librarymanage.model.table.TableColumn.column;
import static com.nhj.librarymanage.model.table.TableViewSupport.columns;
import static com.nhj.librarymanage.model.table.TableViewSupport.search;

public enum BorrowListTableView implements TableViewSpec {

    TABLE(
            search(
                    field("bookTitle", "도서명"),
                    field("memberName", "회원명")
            ),
            columns(
                    TableColumn.leading(""),
                    column("대출 ID"),
                    column("재고 ID"),
                    column("저자"),
                    column("출판사"),
                    column("출판사"),
                    column("출판사"),
                    column("출판사"),
                    column("출판사")
            )
    );

    private final List<SearchField> searchFields;
    private final List<TableColumn> columns;

    BorrowListTableView(
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