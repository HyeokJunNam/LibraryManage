package com.nhj.librarymanage.model.table;

import com.nhj.librarymanage.domain.entity.Member;

import java.util.List;

import static com.nhj.librarymanage.model.table.SearchField.field;
import static com.nhj.librarymanage.model.table.TableColumn.column;
import static com.nhj.librarymanage.model.table.TableViewSupport.columns;
import static com.nhj.librarymanage.model.table.TableViewSupport.search;

public enum MemberSearchModalView implements TableViewSpec {

    TABLE(
            search(
                    field(Member.Fields.name, "이름"),
                    field(Member.Fields.email, "이메일"),
                    field(Member.Fields.phoneNumber, "연락처")
            ),
            columns(
                    TableColumn.leading(""),
                    column("이름"),
                    column("이메일"),
                    column("연락처"),
                    column("권한"),
                    column("가입일")
            )
    );

    private final List<SearchField> searchFields;
    private final List<TableColumn> columns;

    MemberSearchModalView(
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