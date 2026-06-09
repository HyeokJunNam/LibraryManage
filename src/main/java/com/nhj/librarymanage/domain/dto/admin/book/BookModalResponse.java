package com.nhj.librarymanage.domain.dto.admin.book;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookModalResponse {

    public record ListItem(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            int stockQuantity,
            int availableQuantity
    ) {
        public static ListItem from(Book book) {
            int availableQuantity = 0;
            int stockQuantity = 0;

            for (BookCopy bookCopy : book.getBookCopies()) {
                stockQuantity++;

                if (bookCopy.isBorrowable()) {
                    availableQuantity++;
                }
            }

            return new ListItem(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    stockQuantity,
                    availableQuantity
            );
        }
    }

}