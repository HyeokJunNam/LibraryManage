package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.dto.BorrowResponse;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BorrowHistory;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.error.ErrorCode;
import com.nhj.librarymanage.error.exception.InvalidStateException;
import com.nhj.librarymanage.repository.BookRepository;
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

    private final BookRepository bookRepository;
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

    private boolean isBorrowed(Book book) {
        //return bookEntity.getBorrowHistoryEntity() != null;
        return true;
    }

    @Transactional
    public void borrow(BorrowRequest.Borrow borrow) {
        Book book = bookRepository.get(borrow.getBookId());

        if (isBorrowed(book)) {
            throw new InvalidStateException(ErrorCode.BORROWED_BOOK);
        }

        Member member = memberRepository.get(borrow.getMemberId());

        book.startBorrow(member, BORROW_DAY);
    }

    @Transactional
    public void returnBook(BorrowRequest.ReturnBook returnBook) {
        Book book = bookRepository.get(returnBook.getBookId());

        if (!isBorrowed(book)) {
            throw new InvalidStateException(ErrorCode.NOT_BORROWED_BOOK);
        }

        book.endBorrow();
    }

}
