package com.nhj.librarymanage.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ReturnStatus implements LabelEnum {

    BORROWED("대출 중"),
    OVERDUE("연체"),
    RETURNED("반납 완료");
    ;

    private final String label;

    public static List<EnumOption<ReturnStatus>> options() {
        return Arrays.stream(values())
                .map(status -> new EnumOption<>(status, status.getLabel()))
                .toList();
    }

}
