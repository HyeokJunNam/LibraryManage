package com.nhj.librarymanage.domain.code;

import java.util.List;

public record AdminPageOptions(
        List<EnumOption<BookItemStatus>> bookItemStatuses,
        List<EnumOption<BorrowStatus>> borrowStatuses
) {
    public static AdminPageOptions options() {
        return new AdminPageOptions(
                BookItemStatus.options(),
                BorrowStatus.options()
        );
    }
}