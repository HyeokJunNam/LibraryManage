package com.nhj.librarymanage.domain.dto.admin.book;

import com.nhj.librarymanage.model.table.SearchField;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.ui.Model;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookManageRequest {

    @FieldNameConstants
    public record Search(
            String title,
            String isbn,
            String author,
            String publisher
    ) {
        public static void addSearchFieldsTo(Model model) {
            List<SearchField> searchFields = List.of(
                    SearchField.of(Fields.title, "도서명"),
                    SearchField.of(Fields.isbn, "ISBN"),
                    SearchField.of(Fields.author, "저자"),
                    SearchField.of(Fields.publisher, "출판사")
            );

            model.addAttribute("searchFields", searchFields);
        }

        public void applyTo(Model model) {
            addSearchFieldsTo(model);
            model.addAttribute("searchCondition", this);
        }
    }

    public record Create(
            List<Item> items
    ) {
        public record Item(
                String isbn,
                String title,
                String author,
                String publisher,
                String description,
                String thumbnailUrl
        ) {
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Update {
        private long id;
        private String name;

    }

}
