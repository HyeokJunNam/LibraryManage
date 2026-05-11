package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.BookCopyRequest;
import com.nhj.librarymanage.domain.model.dto.BookCopyResponse;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.BookItemAlreadyBorrowedException;
import com.nhj.librarymanage.repository.BookCopyRepository;
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
public class BookCopyService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    @Transactional
    public PageResponse<BookCopyResponse.Info> getBookCopies(Long bookId, Pageable pageable) {
        List<BookCopy> bookCopies = bookCopyRepository.findAllByBookId(bookId);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), bookCopies.size());

        List<BookCopy> pageContent =
                start >= bookCopies.size()
                        ? List.of()
                        : bookCopies.subList(start, end);

        Page<BookCopyResponse.Info> page = new PageImpl<>(
                pageContent,
                pageable,
                bookCopies.size()
        ).map(BookCopyResponse.Info::from);

        return PageResponse.from(page);
    }

    @Transactional
    public BookCopyResponse.Quantity getBookQuantity(Long bookId) {
        List<BookCopy> bookCopies = bookCopyRepository.findAllByBookId(bookId);

        int stockQuantity = 0;
        int borrowedQuantity = 0;
        int availableQuantity = 0;

        for (BookCopy bookCopy : bookCopies) {
            stockQuantity++;

            switch (bookCopy.getBorrowStatus()) {
                case AVAILABLE -> availableQuantity++;
                case BORROWED -> borrowedQuantity++;
            }
        }

        return new BookCopyResponse.Quantity(
                stockQuantity,
                borrowedQuantity,
                availableQuantity
        );
    }


    @Transactional
    public void upsetBookCopy(Long bookId, BookCopyRequest.Upsert upsert) {
        createBookCopy(bookId, upsert.createItems());
        updateBookCopy(bookId, upsert.updateItems());
        deleteBookCopy(upsert.deleteIds());
    }

    @Transactional
    public void createBookCopy(Long bookId, List<BookCopyRequest.Upsert.CreateItem> createItems) {
        Book book = bookRepository.getById(bookId);
        List<BookCopy> bookCopies = new ArrayList<>();

        for (BookCopyRequest.Upsert.CreateItem entry : createItems) {
            BookCopy bookCopy = BookCopy.builder()
                    .book(book)
                    .status(entry.status())
                    .location(entry.location())
                    .build();

            bookCopies.add(bookCopy);
        }

        bookCopyRepository.saveAll(bookCopies);
    }

    @Transactional
    public void updateBookCopy(Long bookId, List<BookCopyRequest.Upsert.UpdateItem> updateItems) {
        Book book = bookRepository.getById(bookId);
        List<BookCopy> bookCopies = book.getBookCopies();

        Map<Long, BookCopy> bookItemMap = bookCopies.stream()
                .collect(Collectors.toMap(
                        BookCopy::getId,
                        Function.identity()
                ));


        for (BookCopyRequest.Upsert.UpdateItem entry : updateItems) {
            BookCopy bookCopy = bookItemMap.get(entry.bookItemId());
            validateNotBorrowed(bookCopy);

            bookCopy.update(entry.status(), entry.location());
        }
    }

    @Transactional
    public void deleteBookCopy(List<Long> bookItemIds) {
        if (bookItemIds == null) {
            return ;
        }

        List<BookCopy> bookCopies = bookCopyRepository.findAllById(bookItemIds);

        for (BookCopy bookCopy : bookCopies) {
            validateNotBorrowed(bookCopy);
        }

        bookCopyRepository.deleteAllByIdInBatch(bookItemIds);
    }


    private void validateNotBorrowed(BookCopy bookCopy) {
        if (bookCopy.getBorrowRecord() != null) {
            throw new BookItemAlreadyBorrowedException(BookErrorCode.BOOK_ITEM_ALREADY_BORROWED);
        }
    }


}
