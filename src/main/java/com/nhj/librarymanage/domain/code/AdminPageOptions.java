package com.nhj.librarymanage.domain.code;

import java.util.List;

public record AdminPageOptions(
        List<EnumOption<BookCopyStatus>> bookCopyStatuses,
        List<EnumOption<BorrowStatus>> borrowStatuses
) {
    public static AdminPageOptions options() {
        return new AdminPageOptions(
                BookCopyStatus.options(),
                BorrowStatus.options()
        );
    }
}