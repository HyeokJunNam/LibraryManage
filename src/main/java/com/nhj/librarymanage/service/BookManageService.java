package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookManageService {

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;

    public BookResponse.Info getBook(long id) {
        //return BookResponse.Info.of(bookRepository.get(id));
        return null;
    }

    @Transactional
    public Page<BookResponse.Info> getBooks(BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(searchCondition, pageable);

        List<BookItem> bookItems = bookItemRepository.findAllByBookIn(books.getContent());
        Map<Long, List<BookItem>> bookItemMap = bookItems.stream()
                .collect(Collectors.groupingBy(item -> item.getBook().getId()));

        return books.map(book -> BookResponse.Info.of(book, bookItemMap));
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
