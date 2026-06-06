package com.nhj.librarymanage.domain.dto.member.book;

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
            String publisher
    ) {
        public static ListItem from(Book book) {
            return new ListItem(
                    book.getId(),
                    book.getLocation(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher()
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

            for (BookCopy bookCopy : bookCopies) {
                if (bookCopy.getBorrowRecord() != null) {
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