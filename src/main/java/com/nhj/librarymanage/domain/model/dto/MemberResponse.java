package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Info {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;
        private String loginId;
        private String memberNo;
        private String name;
        private String email;
        private String phone;
        private String role;
        private LocalDateTime createdAt;

        public static Info from(Member member) {
            return Info.builder()
                    .id(member.getId())
                    .loginId(member.getLoginId())
                    .memberNo(member.getMemberNo())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phone("010-4582-8903")
                    .role(member.getRole().name())
                    .createdAt(member.getCreatedAt())
                    .build();
        }

        public static Info of(String loginId, String name) {
            return Info.builder()
                    .loginId(loginId)
                    .name(name)
                    .build();
        }

    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class LoginIdCheck {
        private String loginId;
        private boolean available;

        public static LoginIdCheck of(String loginId, boolean available) {
            return LoginIdCheck.builder()
                    .loginId(loginId)
                    .available(available)
                    .build();
        }
    }

}
