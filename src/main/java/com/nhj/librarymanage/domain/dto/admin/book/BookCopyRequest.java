package com.nhj.librarymanage.domain.dto.admin.book;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookCopyRequest {

    public record Upsert (
            List<CreateItem> createItems,
            List<UpdateItem> updateItems,
            List<Long> deleteIds
    ) {
        public record CreateItem(
                BookCopyCondition bookCopyCondition
        ) {
        }

        public record UpdateItem(
                Long bookCopyId,
                BookCopyCondition bookCopyCondition
        ) {
        }
    }



}
