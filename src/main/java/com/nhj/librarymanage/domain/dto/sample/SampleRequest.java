package com.nhj.librarymanage.domain.dto.sample;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// View -> Repository
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleRequest {

    // 수행 할 작업에 필요한 파라미터 (CRUD 제외)
    @AllArgsConstructor
    @Getter
    public static class Param {

    }

    // 생성 요청
    @AllArgsConstructor
    @Getter
    public static class Create {

    }

    // 조회 요청
    @AllArgsConstructor
    @Getter
    public static class Read {

    }

    // 수정 요청
    @AllArgsConstructor
    @Getter
    public static class Update {

    }

    // 삭제 요청
    @AllArgsConstructor
    @Getter
    public static class Delete {

    }


}