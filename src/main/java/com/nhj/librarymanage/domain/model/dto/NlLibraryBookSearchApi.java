package com.nhj.librarymanage.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NlLibraryBookSearchApi {

    // REST API 요청
    @Builder(access = AccessLevel.PRIVATE)
    @Getter
    public static class Send {

    }

    // REST API 응답
    public record Receive(
        @JsonProperty("PAGE_NO")
        String pageNo,

        @JsonProperty("TOTAL_COUNT")
        String totalCount,

        @JsonProperty("docs")
        List<Doc> docs
    ) {
        public record Doc(
                @JsonProperty("TITLE")
                String title,

                @JsonProperty("VOL")
                String vol,

                @JsonProperty("SERIES_TITLE")
                String seriesTitle,

                @JsonProperty("SERIES_NO")
                String seriesNo,

                @JsonProperty("AUTHOR")
                String author,

                @JsonProperty("EA_ISBN")
                String eaIsbn,

                @JsonProperty("EA_ADD_CODE")
                String eaAddCode,

                @JsonProperty("SET_ISBN")
                String setIsbn,

                @JsonProperty("SET_ADD_CODE")
                String setAddCode,

                @JsonProperty("SET_EXPRESSION")
                String setExpression,

                @JsonProperty("PUBLISHER")
                String publisher,

                @JsonProperty("EDITION_STMT")
                String editionStmt,

                @JsonProperty("PRE_PRICE")
                String prePrice,

                @JsonProperty("KDC")
                String kdc,

                @JsonProperty("DDC")
                String ddc,

                @JsonProperty("PAGE")
                String page,

                @JsonProperty("BOOK_SIZE")
                String bookSize,

                @JsonProperty("FORM")
                String form,

                @JsonProperty("PUBLISH_PREDATE")
                String publishPredate,

                @JsonProperty("SUBJECT")
                String subject,

                @JsonProperty("EBOOK_YN")
                String ebookYn,

                @JsonProperty("CIP_YN")
                String cipYn,

                @JsonProperty("CONTROL_NO")
                String controlNo,

                @JsonProperty("TITLE_URL")
                String titleUrl,

                @JsonProperty("BOOK_TB_CNT")
                String bookTbCnt,

                @JsonProperty("BOOK_INTRODUCTION")
                String bookIntroduction,

                @JsonProperty("BOOK_SUMMARY")
                String bookSummary,

                @JsonProperty("PUBLISHER_URL")
                String publisherUrl,

                @JsonProperty("INPUT_DATE")
                String inputDate,

                @JsonProperty("UPDATE_DATE")
                String updateDate
        ) {

        }
    }

}
