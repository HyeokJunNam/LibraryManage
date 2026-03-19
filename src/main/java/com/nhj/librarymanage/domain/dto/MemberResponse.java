package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.MemberEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    // Service → Controller
    @Builder
    @Getter
    public static class InfoDto {
        private String loginId;
        private String name;

        public static InfoDto from(MemberEntity memberEntity) {
            return InfoDto.builder()
                    .name(memberEntity.getName())
                    .build();
        }

    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}
