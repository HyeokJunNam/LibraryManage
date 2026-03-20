package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookManageService {

    private final BookRepository bookRepository;

    public BookResponse.Info getBook(long id) {
        return BookResponse.Info.toDto(bookRepository.get(id));
    }

    @Transactional
    public List<BookResponse.Info> getBooks() {
        List<Book> bookList = bookRepository.findAll();

        return bookList.stream().map(BookResponse.Info::toDto).toList();
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
