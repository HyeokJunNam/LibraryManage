package com.nhj.librarymanage.domain.model.dto;

import com.nhj.librarymanage.domain.code.BookCopyStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    public record Info(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            int stockQuantity,
            int availableQuantity
    ) {
        public static Info from(Book book) {
            int availableQuantity = 0;
            int stockQuantity = 0;

            for (BookCopy bookCopy : book.getBookCopies()) {
                stockQuantity++;

                if (bookCopy.getStatus() == BookCopyStatus.AVAILABLE
                        && bookCopy.getBorrowRecord() == null) {
                    availableQuantity++;
                }
            }

            return new Info(
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

    public record Detail(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            String description,
            String thumbnailUrl
    ) {
        public static Detail from(Book book) {
            return new Detail(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getDescription(),
                    book.getThumbnailUrl()
            );
        }
    }


}