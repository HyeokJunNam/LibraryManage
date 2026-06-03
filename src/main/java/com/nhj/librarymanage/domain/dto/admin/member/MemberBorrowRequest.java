package com.nhj.librarymanage.domain.dto.admin.member;

import com.nhj.librarymanage.model.table.SearchField;
import com.nhj.librarymanage.util.NumberParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.ui.Model;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberBorrowRequest {

    @FieldNameConstants
    public record Search(
            String bookTitle,
            Long bookRecordId
    ) {
        public void applySearchFields(Model model) {
            List<SearchField> searchFields = List.of(
                    SearchField.of(Fields.bookTitle, "도서명"),
                    SearchField.of(Fields.bookRecordId, "재고 ID")
            );

            model.addAttribute("searchFields", searchFields);
        }
    }

}
