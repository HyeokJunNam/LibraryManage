package com.nhj.librarymanage.domain.dto.admin.book;

import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowRequest;
import com.nhj.librarymanage.model.table.SearchField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.ui.Model;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookBorrowRequest {

    @FieldNameConstants
    public record Search(
            String memberName,
            String memberNo
    ) {
        public void applySearchFields(Model model) {
            List<SearchField> searchFields = List.of(
                    SearchField.of(Fields.memberName, "회원명"),
                    SearchField.of(Fields.memberNo, "회원 번호")
            );

            model.addAttribute("searchFields", searchFields);
        }
    }

}
