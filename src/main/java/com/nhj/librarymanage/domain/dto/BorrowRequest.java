package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.model.table.SearchField;
import com.nhj.librarymanage.util.NumberParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.ui.Model;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorrowRequest {

    @FieldNameConstants
    public record SearchCondition(
            String bookTitle,
            String memberName
    ) {
        public void applySearchFields(Model model) {
            List<SearchField> searchFields = List.of(
                    SearchField.of(Fields.bookTitle, "도서명"),
                    SearchField.of(Fields.memberName, "회원명")
            );

            model.addAttribute("searchFields", searchFields);
        }
    }

    @FieldNameConstants
    public record SearchConditionByMember(
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

    public record Create(
            String memberId,
            List<Item> items
    ) {
        public record Item(
                Long bookId,
                long quantity
        ) {
        }

        public Long memberIdAsLong() {
            return NumberParser.parseLong(memberId);
        }
    }

}
