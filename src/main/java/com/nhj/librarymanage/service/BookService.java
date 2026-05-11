package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.PageResponseTest;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse.Detail getBook(long id) {
        return BookResponse.Detail.from(bookRepository.getById(id));
    }

    @Transactional
    public PageResponse<BookResponse.Info> getBooks(BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(searchCondition, pageable);
        return PageResponse.from(books.map(BookResponse.Info::from));
    }


    @Transactional
    public void createBooks(BookRequest.Create create) {
        List<Book> books = new ArrayList<>();

        for (BookRequest.Create.Item item : create.items()) {
        Book book = Book.builder()
                .isbn(item.isbn())
                .title(item.title())
                .author(item.author())
                .publisher(item.publisher())
                .description(item.description())
                .thumbnailUrl(item.thumbnailUrl())
                .build();

            books.add(book);
        }

        bookRepository.saveAll(books);
    }

    @Transactional
    public void updateBook(BookRequest.Update update) {
        Book book = bookRepository.getById(update.getId());

        book.changeTitle(update.getName());
    }

    @Transactional
    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

}
