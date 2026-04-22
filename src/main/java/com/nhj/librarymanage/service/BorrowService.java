package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowResponse;
import com.nhj.librarymanage.domain.model.event.BookBorrowableEvent;
import com.nhj.librarymanage.domain.model.vo.BorrowBook;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.NotBorrowableException;
import com.nhj.librarymanage.error.exception.book.NotReturnableException;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BookRepository;
import com.nhj.librarymanage.repository.BorrowRecordRepository;
import com.nhj.librarymanage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BorrowService {

    private final ApplicationEventPublisher eventPublisher;

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    private static final long BORROW_DAY = 7;

    @Transactional
    public Page<BorrowResponse.Info> getBorrowHistory(BorrowRequest.Param param, Pageable pageable) {
        boolean onlyBorrowed = param.isBorrowed();
        BorrowRequest.SearchCondition searchCondition = BorrowRequest.SearchCondition.of(onlyBorrowed);

        Page<BorrowRecord> borrows = borrowRecordRepository.search(searchCondition, pageable);

        return borrows.map(BorrowResponse.Info::toDto);
    }

    @Transactional
    public Page<BorrowResponse.Info> getMemberBorrowHistory(Long memberId, Pageable pageable) {
        Page<BorrowRecord> borrows = borrowRecordRepository.searchByMemberId(memberId, pageable);

        return borrows.map(BorrowResponse.Info::toDto);
    }


    // TODO LOCK 처리
    @Transactional
    public void borrow(BorrowRequest.Borrow borrow) {
        List<Long> bookIds = borrow.getBorrowBooks().stream().map(BorrowBook::bookId).toList();
        List<Book> books = bookRepository.findBorrowableBook(bookIds);

        Map<Long, Long> borrowRequestMap = borrow.getBorrowBooks().stream()
                .collect(Collectors.toMap(
                        BorrowBook::bookId,
                        BorrowBook::quantity,
                        Long::sum
                ));

        Member member = memberRepository.getById(borrow.getMemberId());

        for (Book book : books) {
            List<BookItem> bookItems = book.getBookItems();

            long requestQuantity = borrowRequestMap.get(book.getId()); // throw? 잘못된 조회가 있을리는 없지만..?

            // 대여 가능한 책 수
            if (requestQuantity > bookItems.size()) {
                throw new NotBorrowableException(BookErrorCode.BOOK_NOT_BORROWABLE);
            }

            for (BookItem bookItem : bookItems) {
                bookItem.startBorrow(member, BORROW_DAY);
            }
        }
    }

    private boolean isBorrowed(BookItem bookItem) {
        BorrowRecord borrowRecord = bookItem.getBorrowRecord();
        return borrowRecord != null && borrowRecord.getReturnedAt() == null;
    }

    @Transactional
    public void returnBook(BorrowRequest.ReturnBook returnBook) {
        BookItem bookItem = bookItemRepository.getById(returnBook.getBookItemId());

        if (!isBorrowed(bookItem)) {
            throw new NotReturnableException(BookErrorCode.BOOK_NOT_RETURNABLE);
        }

        bookItem.returnBook();
        eventPublisher.publishEvent(new BookBorrowableEvent(bookItem.getBook().getId()));
    }

}
