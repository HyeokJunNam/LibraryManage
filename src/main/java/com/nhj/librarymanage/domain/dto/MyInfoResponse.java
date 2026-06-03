package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyInfoResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Detail {
        private String loginId;
        private String memberNo;
        private String name;
        private String email;
        private String phoneNumber;
        private LocalDateTime createdAt;

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


}
