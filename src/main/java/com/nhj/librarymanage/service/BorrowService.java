package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.dto.BorrowResponse;
import com.nhj.librarymanage.domain.entity.BookEntity;
import com.nhj.librarymanage.domain.entity.BorrowHistoryEntity;
import com.nhj.librarymanage.domain.entity.MemberEntity;
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
    public Page<BorrowResponse.InfoDto> getBorrowHistories(BorrowRequest.ParamDto paramDto, Pageable pageable) {
        boolean onlyBorrowed = paramDto.isBorrowed();
        BorrowRequest.SearchConditionDto searchConditionDto = BorrowRequest.SearchConditionDto.of(onlyBorrowed);

        Page<BorrowHistoryEntity> borrowHistoryEntityPage = borrowHistoryRepository.findAll(searchConditionDto, pageable);

        return borrowHistoryEntityPage.map(BorrowResponse.InfoDto::toDto);
    }

    private boolean isBorrowed(BookEntity bookEntity) {
        return bookEntity.getBorrowHistoryEntity() != null;
    }

    @Transactional
    public void borrow(BorrowRequest.BorrowDto borrowDto) {
        BookEntity bookEntity = bookRepository.get(borrowDto.getBookId());

        if (isBorrowed(bookEntity)) {
            throw new InvalidStateException(ErrorCode.BORROWED_BOOK);
        }

        MemberEntity memberEntity = memberRepository.get(borrowDto.getMemberId());

        bookEntity.startBorrow(memberEntity, BORROW_DAY);
    }

    @Transactional
    public void returnBook(BorrowRequest.ReturnBookDto returnBookDto) {
        BookEntity bookEntity = bookRepository.get(returnBookDto.getBookId());

        if (!isBorrowed(bookEntity)) {
            throw new InvalidStateException(ErrorCode.NOT_BORROWED_BOOK);
        }

        bookEntity.endBorrow();
    }

}
