package com.nhj.librarymanage.domain.dto.member.info;

import com.nhj.librarymanage.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyInfoResponse {

    @Builder(access = AccessLevel.PRIVATE)
    public record Detail(
            String loginId,
            String memberNo,
            String name,
            String email,
            String phoneNumber,
            LocalDateTime createdAt
    ) {
        public static Detail from(Member member) {
            return Detail.builder()
                    .loginId(member.getLoginId())
                    .memberNo(member.getMemberNo())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phoneNumber(member.getPhoneNumber())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }

    public record BorrowStatistics(
            long totalBorrowCount,
            long currentBorrowCount,
            long overdueCount
    ) {

    }


}
