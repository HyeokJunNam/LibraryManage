package com.nhj.librarymanage.domain.model.dto.sample;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Repository -> View
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleResponse {

    // Service → Controller
    @AllArgsConstructor
    @Getter
    public static class Info {

    }

    @AllArgsConstructor
    @Getter
    public static class Detail {

    }

}