package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.dto.BorrowResponse;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowHistory;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.NotBorrowableException;
import com.nhj.librarymanage.error.exception.book.NotReturnableException;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BorrowHistoryRepository;
import com.nhj.librarymanage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BorrowService {

    private final BookItemRepository bookItemRepository;
    private final MemberRepository memberRepository;
    private final BorrowHistoryRepository borrowHistoryRepository;

    private static final long BORROW_DAY = 7;

    @Transactional
    public Page<BorrowResponse.Info> getBorrowHistories(BorrowRequest.Param param, Pageable pageable) {
        boolean onlyBorrowed = param.isBorrowed();
        BorrowRequest.SearchCondition searchCondition = BorrowRequest.SearchCondition.of(onlyBorrowed);

        Page<BorrowHistory> borrowHistoryEntityPage = borrowHistoryRepository.findAll(searchCondition, pageable);

        return borrowHistoryEntityPage.map(BorrowResponse.Info::toDto);
    }

    private boolean isBorrowable(BookItem bookItem) {
        return bookItem.getBorrowHistory() == null && bookItem.getStatus() == BookItemStatus.AVAILABLE;
    }

    @Transactional
    public void borrow(BorrowRequest.Borrow borrow) {
        BookItem bookItem = bookItemRepository.get(borrow.getBookItemId());

        if (!isBorrowable(bookItem)) {
            throw new NotBorrowableException(BookErrorCode.BOOK_NOT_BORROWABLE);
        }

        Member member = memberRepository.getByLoginId(borrow.getLoginId());

        bookItem.startBorrow(member, BORROW_DAY);
    }

    private boolean isBorrowed(BookItem bookItem) {
        BorrowHistory borrowHistory = bookItem.getBorrowHistory();
        return borrowHistory != null && borrowHistory.getReturnedAt() == null;
    }

    @Transactional
    public void returnBook(BorrowRequest.ReturnBook returnBook) {
        BookItem bookItem = bookItemRepository.get(returnBook.getBookItemId());

        if (!isBorrowed(bookItem)) {
            throw new NotReturnableException(BookErrorCode.BOOK_NOT_RETURNABLE);
        }

        bookItem.returnBook();
    }

}
