package com.nhj.librarymanage.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum BookCopyCondition implements LabelEnum  {

    NORMAL("정상"),
    DAMAGED("파손"),
    LOST("분실"),
    DISCARDED("폐기"),
    ;

    private final String label;

    public String getCode() {
        return name();
    }

    public static List<EnumOption<BookCopyCondition>> options() {
        return Arrays.stream(values())
                .map(status -> new EnumOption<>(status, status.getLabel()))
                .toList();
    }

}
