package com.nhj.librarymanage.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberStatistics {

    private long totalMemberCount; // 전체 회원 수
    private long todayBorrowMemberCount; // 당일 대출 회원 수
    private long currentBorrowMemberCount; // 현재 대출 회원 수
    private long currentOverdueMemberCount; // 현재 연체 회원 수

}
