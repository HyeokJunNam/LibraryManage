package com.nhj.librarymanage.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BorrowStatistics {

    private long totalBookItemCount; // 도서 재고 수
    private long totalBorrowCount; // 대출 가능 수
    private long currentBorrowCount; // 대출 중 재고
    private long overdueBorrowCount; // 연체 재고

}
