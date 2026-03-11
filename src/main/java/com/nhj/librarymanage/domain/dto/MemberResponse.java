package com.nhj.librarymanage.domain.dto;

import com.nhj.librarymanage.domain.entity.MemberEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    // Service → Controller
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class InfoDto {
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
