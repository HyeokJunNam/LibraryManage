package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookItemRequest {

    // 생성 + 수정
    public record Upsert (
            List<CreateEntry> createItems,
            List<UpdateEntry> updateItems,
            List<Long> deleteItemIds

    ) {
        public record CreateEntry(
                BookItemStatus status,
                String location
        ) {
        }

        public record UpdateEntry(
                Long bookItemId,
                BookItemStatus status,
                String location
        ) {
        }
    }

}
