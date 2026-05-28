package com.nhj.librarymanage.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DashboardStatistics {

    private long currentBorrowCount;
    private long currentOverdueCount;
    private long currentBorrowMemberCount;
    private long currentOverdueMemberCount;

    private long todayBorrowCount;
    private long todayReturnDueCount;
    private long renewalRequestCount;



}
