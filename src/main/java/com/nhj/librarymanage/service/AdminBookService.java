package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.admin.book.BookManageRequest;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageResponse;
import com.nhj.librarymanage.domain.dto.admin.book.BookModalResponse;
import com.nhj.librarymanage.domain.dto.admin.book.BookSearch;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
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
public class AdminBookService {

    private final BookRepository bookRepository;

    public BookManageResponse.Detail getBook(Long id) {
        return BookManageResponse.Detail.from(bookRepository.getById(id));
    }

    public PageResponse<BookManageResponse.ListItem> getBooks(BookSearch search, Pageable pageable) {
        Page<Book> books = bookRepository.search(search, pageable);
        return PageResponse.from(books.map(BookManageResponse.ListItem::from));
    }

    @Transactional
    public PageResponse<BookModalResponse.ListItem> getBooksForSearchModal(BookSearch search, Pageable pageable) {
        Page<Book> books = bookRepository.search(search, pageable);
        return PageResponse.from(books.map(BookModalResponse.ListItem::from));
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
