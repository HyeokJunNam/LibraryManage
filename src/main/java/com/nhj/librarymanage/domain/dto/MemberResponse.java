package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.Member;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    // Service → Controller
    @Builder
    @Getter
    public static class InfoDto {
        private String loginId;
        private String name;

        public static InfoDto from(Member member) {
            return InfoDto.builder()
                    .name(member.getName())
                    .build();
        }

    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}
