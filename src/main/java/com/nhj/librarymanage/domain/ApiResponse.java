package com.nhj.librarymanage.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nhj.librarymanage.domain.model.dto.PageResponse;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@JsonPropertyOrder({"timestamp", "code", "result"})
@Getter
public class ApiResponse {

    private final ZonedDateTime timestamp;
    private final String code;
    private final Object result;

    private ApiResponse(ZonedDateTime timestamp, String code, Object result) {
        this.timestamp = timestamp != null ? timestamp : ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.code = code != null ? code : "SUCCESS";
        this.result = result;
    }

    public static ApiResponse result(Object result) {
        return new ApiResponse(null, null, result);
    }

    public static <T> ApiResponse result(Page<T> result) {
        return new ApiResponse(null, null, PageResponse.from(result));
    }

    public static Builder code(String code) {
        return new Builder().code(code);
    }

    public static class Builder {
        private ZonedDateTime timestamp;
        private String code;

        public Builder timestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public ApiResponse result(Object result) {
            return new ApiResponse(timestamp, code, result);
        }
    }

}