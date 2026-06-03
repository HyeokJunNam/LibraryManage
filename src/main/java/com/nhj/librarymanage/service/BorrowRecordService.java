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

@RequiredArgsConstructor
@Service
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;

    public PageResponse<BorrowResponse.History> getBorrows(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.search(searchCondition, pageable);
        Page<BorrowResponse.History> histories = borrowRecords.map(BorrowResponse.History::from);

        return PageResponse.from(histories);
    }

    public PageResponse<BorrowResponse.MemberHistory> getBorrowsByMember(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByMemberId(memberId, searchCondition, pageable);
        Page<BorrowResponse.MemberHistory> histories = borrowRecords.map(BorrowResponse.MemberHistory::from);

        return PageResponse.from(histories);
    }

    public PageResponse<BorrowResponse.MyRecord> getMyBorrows(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByMemberId(memberId, searchCondition, pageable);
        Page<BorrowResponse.MyRecord> histories = borrowRecords.map(BorrowResponse.MyRecord::from);

        return PageResponse.from(histories);
    }

    public PageResponse<BorrowResponse.Returnable> getReturnableBorrowsByMember(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchReturnableByMemberId(memberId, searchCondition, pageable);
        Page<BorrowResponse.Returnable> returnables = borrowRecords.map(BorrowResponse.Returnable::from);

        return PageResponse.from(returnables);
    }

    public PageResponse<BorrowResponse.BookHistory> getBorrowsByBook(Long bookId, BorrowRequest.SearchConditionByBook searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByBookId(bookId, searchCondition, pageable);
        Page<BorrowResponse.BookHistory> histories = borrowRecords.map(BorrowResponse.BookHistory::from);

        return PageResponse.from(histories);
    }

    public PageResponse<BorrowResponse.OverdueHistory> getOverdueBorrows(BorrowRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchOverdueBorrowRecords(searchCondition, pageable);
        Page<BorrowResponse.OverdueHistory> histories = borrowRecords.map(BorrowResponse.OverdueHistory::from);

        return PageResponse.from(histories);
    }

    public BorrowStatistics getBorrowStatistics() {
        return borrowRecordRepository.getBorrowStatistics();
    }


}
