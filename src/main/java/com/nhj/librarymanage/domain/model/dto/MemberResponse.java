package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Info {
        private String loginId;
        private String name;

        public static Info from(Member member) {
            return Info.builder()
                    .loginId(member.getLoginId())
                    .name(member.getName())
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
