package com.nhj.librarymanage.domain.model.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReturnRequest {

    public record Create(
            List<Long> bookRecordIds
    ) {
    }

}
