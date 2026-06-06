package com.nhj.librarymanage.domain.dto.member.info;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyInfoRequest {

    public record Update(
            String phoneNumber,
            String newPassword
    ) {
    }

}
