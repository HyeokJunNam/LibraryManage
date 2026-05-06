package com.nhj.librarymanage.domain.code;

public record EnumOption<T extends Enum<T> & LabelEnum>(
        T value,
        String label
) {

    public static <T extends Enum<T> & LabelEnum> EnumOption<T> from(T value) {
        return new EnumOption<>(value, value.getLabel());
    }

}
