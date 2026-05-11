package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookCopyStatus;
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
                BookCopyStatus status,
                String location
        ) {
        }

        public record UpdateItem(
                Long bookItemId,
                BookCopyStatus status,
                String location
        ) {
        }
    }



}
