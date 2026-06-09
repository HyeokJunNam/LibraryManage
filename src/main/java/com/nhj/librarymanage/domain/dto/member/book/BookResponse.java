package com.nhj.librarymanage.domain.dto.member.book;

import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {

    public record ListItem(
            Long id,
            String location,
            String isbn,
            String title,
            String author,
            String publisher,
            boolean borrowable
    ) {
        public static ListItem from(Book book) {
            List<BookCopy> bookCopies = book.getBookCopies();

            boolean borrowable = false;

            // 일단 하나라도 대출 가능한 상태인 경우 허용.
            for (BookCopy bookCopy : bookCopies) {
                if (bookCopy.isBorrowable()) {
                    borrowable = true;
                    break;
                }
            }


            return new ListItem(
                    book.getId(),
                    book.getLocation(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    borrowable
            );
        }
    }

    public record Detail(
            Long id,
            String location,
            String isbn,
            String title,
            String author,
            String publisher,
            String description,
            String thumbnailUrl,
            boolean borrowable
    ) {
        public static Detail from(Book book) {
            List<BookCopy> bookCopies = book.getBookCopies();

            boolean borrowable = false;

            // 일단 하나라도 대출 가능한 상태인 경우 허용.
            for (BookCopy bookCopy : bookCopies) {
                if (bookCopy.isBorrowable()) {
                    borrowable = true;
                    break;
                }
            }

            return new Detail(
                    book.getId(),
                    book.getLocation(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getDescription(),
                    book.getThumbnailUrl(),
                    borrowable
            );
        }
    }


}