package com.nhj.librarymanage.domain.dto.member.book;

import com.nhj.librarymanage.model.table.SearchField;
import lombok.experimental.FieldNameConstants;
import org.springframework.ui.Model;

import java.util.List;

@FieldNameConstants
public record BookSearch(
        String title,
        String isbn,
        String author,
        String publisher
) {
    public static void addSearchFieldsTo(Model model) {
        List<SearchField> searchFields = List.of(
                SearchField.of(BookSearch.Fields.title, "도서명"),
                SearchField.of(BookSearch.Fields.isbn, "ISBN"),
                SearchField.of(BookSearch.Fields.author, "저자"),
                SearchField.of(BookSearch.Fields.publisher, "출판사")
        );

        model.addAttribute("searchFields", searchFields);
    }

    public void applyTo(Model model) {
        addSearchFieldsTo(model);
        model.addAttribute("searchCondition", this);
    }
}