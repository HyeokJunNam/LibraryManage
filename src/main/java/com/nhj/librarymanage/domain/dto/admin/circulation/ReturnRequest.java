package com.nhj.librarymanage.domain.dto.admin.circulation;

import com.nhj.librarymanage.util.NumberParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReturnRequest {

    public record Create(
            List<String> bookRecordIds
    ) {
        public List<Long> bookRecordIdsAsLong() {
            return bookRecordIds.stream().map(NumberParser::parseLong).toList();
        }
    }

}
