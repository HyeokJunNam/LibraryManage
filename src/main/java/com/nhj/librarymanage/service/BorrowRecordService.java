package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowRecordResponse;
import com.nhj.librarymanage.domain.model.PageContent;
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

import java.util.List;

@RequiredArgsConstructor
@Service
public class BorrowRecordService {

    private final ApplicationEventPublisher eventPublisher;

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;


    /*@Transactional
    public Page<BorrowRecordResponse.InfoList> getBorrowRecords(BorrowRequest.Param param, Pageable pageable) {
        boolean onlyBorrowed = param.isBorrowed();
        BorrowRequest.SearchCondition searchCondition = BorrowRequest.SearchCondition.of(onlyBorrowed);

        Page<BorrowRecord> borrows = borrowRecordRepository.search(searchCondition, pageable);

        return borrows.map(BorrowRecordResponse.InfoList::toDto);
    }
    */

    @Transactional
    public PageContent<BorrowRecordResponse.MemberSummary> getBorrowRecordsByMember(Long memberId, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByMemberId(memberId, pageable);
        Page<BorrowRecordResponse.MemberSummary> summaries = borrowRecords.map(BorrowRecordResponse.MemberSummary::from);

        return PageContent.from(summaries);
    }

    @Transactional
    public PageContent<BorrowRecordResponse.BookSummary> getBorrowRecordsByBook(Long bookId, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByBookId(bookId, pageable);
        Page<BorrowRecordResponse.BookSummary> summaries = borrowRecords.map(BorrowRecordResponse.BookSummary::from);

        return PageContent.from(summaries);
    }


}
