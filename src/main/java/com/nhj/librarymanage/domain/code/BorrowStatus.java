package com.nhj.librarymanage.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum BorrowStatus implements LabelEnum {

    AVAILABLE("대출 가능"),
    BORROWED("대출 중"),
    UNAVAILABLE("대출 불가"), // 이게 언제필요한거지??????? 필요없을듯?
    ;

    private final String label;

    public String getCode() {
        return name();
    }

    public static List<EnumOption<BorrowStatus>> options() {
        return Arrays.stream(values())
                .map(status -> new EnumOption<>(status, status.getLabel()))
                .toList();
    }

}
