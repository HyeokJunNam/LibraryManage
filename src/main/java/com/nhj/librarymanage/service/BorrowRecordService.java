package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.dto.PageResponse;
import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.dto.BorrowResponse;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import com.nhj.librarymanage.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional
    public PageResponse<BorrowResponse.History> getBorrowHistory(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.search(searchCondition, pageable);
        Page<BorrowResponse.History> histories = borrowRecords.map(BorrowResponse.History::from);

        return PageResponse.from(histories);
    }

    @Transactional
    public PageResponse<BorrowResponse.MemberHistory> getBorrowHistoryByMember(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByMemberId(memberId, searchCondition, pageable);
        Page<BorrowResponse.MemberHistory> histories = borrowRecords.map(BorrowResponse.MemberHistory::from);

        return PageResponse.from(histories);
    }

    @Transactional
    public PageResponse<BorrowResponse.Returnable> getReturnableBooksByMember(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchReturnableByMemberId(memberId, searchCondition, pageable);
        Page<BorrowResponse.Returnable> returnables = borrowRecords.map(BorrowResponse.Returnable::from);

        return PageResponse.from(returnables);
    }

    @Transactional
    public PageResponse<BorrowResponse.BookHistory> getBorrowHistoryByBook(Long bookId, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByBookId(bookId, pageable);
        Page<BorrowResponse.BookHistory> infos = borrowRecords.map(BorrowResponse.BookHistory::from);

        return PageResponse.from(infos);
    }


    public BorrowStatistics getBorrowStatistics() {
        return borrowRecordRepository.getBorrowStatistics();
    }

    public PageResponse<BorrowResponse.OverdueHistory> getOverdueBorrowRecords(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchOverdueBorrowRecords(searchCondition, pageable);
        Page<BorrowResponse.OverdueHistory> infos = borrowRecords.map(BorrowResponse.OverdueHistory::from);

        return PageResponse.from(infos);

    }


}
