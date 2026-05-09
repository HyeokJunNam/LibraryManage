package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.BookItemRequest;
import com.nhj.librarymanage.domain.model.dto.BookItemResponse;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.BookItemAlreadyBorrowedException;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public PageResponse<BookItemResponse.Summary> getBookItems(Long bookId, Pageable pageable) {
        List<BookItem> bookItems = bookItemRepository.findAllByBookId(bookId);

        int stockQuantity = 0;
        int borrowedQuantity = 0;
        int availableQuantity = 0;

        List<BookItemResponse.Summary> summaries = new ArrayList<>();

        for (BookItem bookItem : bookItems) {
            stockQuantity++;

            switch (bookItem.getBorrowStatus()) {
                case AVAILABLE -> availableQuantity++;
                case BORROWED -> borrowedQuantity++;
            }

            summaries.add(BookItemResponse.Summary.from(bookItem));
        }

        BookItemResponse.Attribute attribute = new BookItemResponse.Attribute(
                stockQuantity,
                borrowedQuantity,
                availableQuantity
        );

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), summaries.size());

        List<BookItemResponse.Summary> pageContent =
                start >= summaries.size()
                        ? List.of()
                        : summaries.subList(start, end);

        Page<BookItemResponse.Summary> page = new PageImpl<>(
                pageContent,
                pageable,
                summaries.size()
        );

        return PageResponse.of(page, attribute);

    }

    @Transactional
    public void upsetBookItem(Long bookId, BookItemRequest.Upsert upsert) {
        createBookItem(bookId, upsert.createItems());
        updateBookItem(bookId, upsert.updateItems());
        deleteBookItem(upsert.deleteItemIds());
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
            validateNotBorrowed(bookItem);

            bookItem.update(entry.status(), entry.location());
        }
    }

    @Transactional
    public void deleteBookItem(List<Long> bookItemIds) {
        List<BookItem> bookItems = bookItemRepository.findAllById(bookItemIds);

        for (BookItem bookItem : bookItems) {
            validateNotBorrowed(bookItem);
        }

        bookItemRepository.deleteAllByIdInBatch(bookItemIds);
    }


    private void validateNotBorrowed(BookItem bookItem) {
        if (bookItem.getBorrowRecord() != null) {
            throw new BookItemAlreadyBorrowedException(BookErrorCode.BOOK_ITEM_ALREADY_BORROWED);
        }
    }


}
