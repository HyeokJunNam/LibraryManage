package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.domain.entity.Book;
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
        return BookResponse.Detail.from(bookRepository.get(id));
    }

    @Transactional
    public Page<BookResponse.Info> getBooks(BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(searchCondition, pageable);
        return books.map(BookResponse.Info::from);
    }


    @Transactional
    public void createBook(List<BookRequest.Create> creates) {
        List<Book> books = new ArrayList<>();

        for (BookRequest.Create create : creates) {
        Book book = Book.builder()
                .isbn(create.getIsbn())
                .title(create.getTitle())
                .author(create.getAuthor())
                .publisher(create.getPublisher())
                .location(create.getLocation())
                .build();

            books.add(book);
        }

        bookRepository.saveAll(books);
    }

    @Transactional
    public void updateBook(BookRequest.Update update) {
        Book book = bookRepository.get(update.getId());

        book.changeTitle(update.getName());
    }

    @Transactional
    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

}
