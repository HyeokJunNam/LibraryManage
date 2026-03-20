package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    // Service → Controller
    @Builder
    @Getter
    public static class Info {
        private String loginId;
        private String name;

        public static Info from(Member member) {
            return Info.builder()
                    .name(member.getName())
                    .build();
        }

    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}
