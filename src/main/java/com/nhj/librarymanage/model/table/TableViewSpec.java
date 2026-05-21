package com.nhj.librarymanage.model.table;

import org.springframework.ui.Model;

import java.util.List;

public interface TableViewSpec {

    List<SearchField> getSearchFields();

    List<TableColumn> getColumns();

    default void applyTo(Model model) {
        model.addAttribute("searchFields", getSearchFields());
        model.addAttribute("columns", getColumns());
    }
}
