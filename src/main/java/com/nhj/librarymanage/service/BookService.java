package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.dto.PageResponse;
import com.nhj.librarymanage.domain.dto.BookManageRequest;
import com.nhj.librarymanage.domain.dto.BookManageResponse;
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
    public BookManageResponse.Detail getBook(long id) {
        return BookManageResponse.Detail.from(bookRepository.getById(id));
    }

    @Transactional
    public PageResponse<BookManageResponse.Info> getBooks(BookManageRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(searchCondition, pageable);
        return PageResponse.from(books.map(BookManageResponse.Info::from));
    }


    @Transactional
    public void createBooks(BookManageRequest.Create create) {
        List<Book> books = new ArrayList<>();

        for (BookManageRequest.Create.Item item : create.items()) {
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
    public void updateBook(BookManageRequest.Update update) {
        Book book = bookRepository.getById(update.getId());

        book.changeTitle(update.getName());
    }

    @Transactional
    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

}
