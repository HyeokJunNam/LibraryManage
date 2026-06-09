package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.dto.admin.book.BookCopyRequest;
import com.nhj.librarymanage.domain.dto.admin.book.BookCopyResponse;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.BookItemAlreadyBorrowedException;
import com.nhj.librarymanage.model.event.BookBorrowableEvent;
import com.nhj.librarymanage.repository.BookCopyRepository;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookCopyService {

    private final ApplicationEventPublisher eventPublisher;

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    private final NotificationSender notificationSender;

    @Transactional
    public PageResponse<BookCopyResponse.ListItem> getBookCopies(Long bookId, Pageable pageable) {
        List<BookCopy> bookCopies = bookCopyRepository.findAllByBookId(bookId);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), bookCopies.size());

        List<BookCopy> pageContent =
                start >= bookCopies.size()
                        ? List.of()
                        : bookCopies.subList(start, end);

        Page<BookCopyResponse.ListItem> page = new PageImpl<>(
                pageContent,
                pageable,
                bookCopies.size()
        ).map(BookCopyResponse.ListItem::from);

        return PageResponse.from(page);
    }


    @Transactional
    public void upsertBookCopy(Long bookId, BookCopyRequest.Upsert upsert) {
        createBookCopy(bookId, upsert.createItems());
        updateBookCopy(upsert.updateItems());
        deleteBookCopy(upsert.deleteIds());

        boolean hasCreatedItems = !upsert.createItems().isEmpty();

        boolean hasNormalCondition = upsert.updateItems().stream()
                .anyMatch(item -> item.bookCopyCondition() == BookCopyCondition.NORMAL);

        if (hasCreatedItems || hasNormalCondition) {
            eventPublisher.publishEvent(new BookBorrowableEvent(bookId));
        }
    }

    @Transactional
    public void createBookCopy(Long bookId, List<BookCopyRequest.Upsert.CreateItem> createItems) {
        Book book = bookRepository.getById(bookId);
        List<BookCopy> bookCopies = new ArrayList<>();

        for (BookCopyRequest.Upsert.CreateItem entry : createItems) {
            BookCopy bookCopy = BookCopy.builder()
                    .book(book)
                    .borrowStatus(BorrowStatus.AVAILABLE)
                    .bookCopyCondition(entry.bookCopyCondition())
                    .build();

            bookCopies.add(bookCopy);
        }

        bookCopyRepository.saveAll(bookCopies);
    }

    @Transactional
    public void updateBookCopy(List<BookCopyRequest.Upsert.UpdateItem> updateItems) {
        Map<Long, BookCopyCondition> bookCopyMap = updateItems.stream()
                .collect(Collectors.toMap(
                        BookCopyRequest.Upsert.UpdateItem::bookCopyId,
                        BookCopyRequest.Upsert.UpdateItem::bookCopyCondition
                ));

        List<BookCopy> bookCopies = bookCopyRepository.findAllById(bookCopyMap.keySet());


        for (BookCopy bookCopy : bookCopies) {
            validateNotBorrowed(bookCopy);
            bookCopy.update(bookCopyMap.get(bookCopy.getId()));
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
        if (bookCopy.getBorrowStatus() == BorrowStatus.BORROWED) {
            throw new BookItemAlreadyBorrowedException(BookErrorCode.BOOK_COPY_ALREADY_BORROWED);
        }
    }


}
