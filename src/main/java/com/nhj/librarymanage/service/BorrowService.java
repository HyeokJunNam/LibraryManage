package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.entity.BookEntity;
import com.nhj.librarymanage.domain.entity.MemberEntity;
import com.nhj.librarymanage.repository.BookRepository;
import com.nhj.librarymanage.repository.BorrowHistoryRepository;
import com.nhj.librarymanage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
    public void borrow(BorrowRequest.BorrowDto borrowDto) {
        BookEntity bookEntity = bookRepository.get(borrowDto.getBookId());
        MemberEntity memberEntity = memberRepository.get(borrowDto.getMemberId());

        bookEntity.startBorrow(memberEntity, BORROW_DAY);
    }

    @Transactional
    public void returnBook(BorrowRequest.ReturnBookDto returnBookDto) {
        BookEntity bookEntity = bookRepository.get(returnBookDto.getBookId());

        bookEntity.endBorrow();
    }

}
