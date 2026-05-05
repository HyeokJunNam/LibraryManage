package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.model.dto.BookItemRequest;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookItemService {

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;

    @Transactional
    public void upsetBookItem(Long bookId, BookItemRequest.Upsert upsert) {
        createBookItem(bookId, upsert.createItems());
        updateBookItem(bookId, upsert.updateItems());
    }

    @Transactional
    public void createBookItem(Long bookId, List<BookItemRequest.Upsert.CreateEntry> creates) {
        Book book = bookRepository.getById(bookId);

        List<BookItem> bookItems = new ArrayList<>();

        for (BookItemRequest.Upsert.CreateEntry entry : creates) {
            BookItem bookItem = BookItem.builder()
                    .book(book)
                    .status(entry.status())
                    .location(entry.location())
                    .build();

            bookItems.add(bookItem);
        }

        bookItemRepository.saveAll(bookItems);
    }

    @Transactional
    public void updateBookItem(Long bookId, List<BookItemRequest.Upsert.UpdateEntry> updates) {
        Book book = bookRepository.getById(bookId);
        List<BookItem> bookItems = book.getBookItems();

        Map<Long, BookItem> bookItemMap = bookItems.stream()
                .collect(Collectors.toMap(
                        BookItem::getId,
                        Function.identity()
                ));


        for (BookItemRequest.Upsert.UpdateEntry entry : updates) {
            BookItem bookItem = bookItemMap.get(entry.bookItemId());
            bookItem.update(entry.status(), entry.location());
        }
    }



}
