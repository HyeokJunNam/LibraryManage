package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.model.dto.BookCreateEntry;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import com.nhj.librarymanage.domain.model.dto.BookResponse;
import com.nhj.librarymanage.repository.BookItemRepository;
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
    private final BookItemRepository bookItemRepository;

    @Transactional
    public BookResponse.Detail getBook(long id) {
        return BookResponse.Detail.from(bookRepository.getById(id));
    }

    @Transactional
    public Page<BookResponse.Info> getBooks(BookRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(searchCondition, pageable);
        return books.map(BookResponse.Info::from);
    }


    @Transactional
    public void createBooks(BookRequest.Create create) {
        List<Book> books = new ArrayList<>();
        List<BookItem> bookItems = new ArrayList<>();

        for (BookCreateEntry entry : create.getBookCreateEntries()) {
        Book book = Book.builder()
                .isbn(entry.getIsbn())
                .title(entry.getTitle())
                .author(entry.getAuthor())
                .publisher(entry.getPublisher())
                .location(entry.getLocation())
                .description(entry.getDescription())
                .thumbnailUrl(entry.getThumbnailUrl())
                .build();

            books.add(book);

            int quantity = entry.getStockQuantity();
            while (quantity > 0)  {
                BookItem bookItem = BookItem.builder()
                        .book(book)
                        .build();

                bookItems.add(bookItem);
                quantity--;
            }
        }

        bookRepository.saveAll(books);
        bookItemRepository.saveAll(bookItems);
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
