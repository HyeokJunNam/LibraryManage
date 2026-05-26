package com.nhj.librarymanage.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BorrowStatistics {

    private long totalBookItemCount; // 도서 재고 수
    private long totalBorrowCount; // 누적 대출 수
    private long currentBorrowCount; // 현재 대출 수
    private long overdueBorrowCount; // 연체 대출 수

}
