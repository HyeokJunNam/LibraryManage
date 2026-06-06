package com.nhj.librarymanage.domain.dto.admin.member;

import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberManageResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    public record ListItem(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String loginId,
            String memberNo,
            String name,
            String email,
            String phoneNumber,
            String role,
            LocalDateTime createdAt
    ) {
        public static ListItem from(Member member) {
            return ListItem.builder()
                    .id(member.getId())
                    .loginId(member.getLoginId())
                    .memberNo(member.getMemberNo())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phoneNumber(member.getPhoneNumber())
                    .role(member.getRole().name())
                    .createdAt(member.getCreatedAt())
                    .build();
        }

        public static ListItem of(String loginId, String name) {
            return ListItem.builder()
                    .loginId(loginId)
                    .name(name)
                    .build();
        }

    }


    @Builder(access = AccessLevel.PRIVATE)
    public record Detail (
            Long id,
            String loginId,
            String memberNo,
            String name,
            String email,
            String phoneNumber,
            String role,
            LocalDateTime createdAt
    ) {
        public static Detail from(Member member) {
            return Detail.builder()
                    .id(member.getId())
                    .loginId(member.getLoginId())
                    .memberNo(member.getMemberNo())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phoneNumber(member.getPhoneNumber())
                    .role(member.getRole().name())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record LoginIdCheck(
            String loginId,
            boolean available
    ) {
        public static LoginIdCheck of(String loginId, boolean available) {
            return LoginIdCheck.builder()
                    .loginId(loginId)
                    .available(available)
                    .build();
        }
    }

}
