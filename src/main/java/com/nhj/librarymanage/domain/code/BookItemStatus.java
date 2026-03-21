package com.nhj.librarymanage.domain.code;

public enum BookItemStatus {

    AVAILABLE,
    DAMAGED,
    LOST,
    DISCARDED,
    ;

    public String getCode() {
        return name();
    }

}
