package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.PageResponse;
import com.nhj.librarymanage.domain.model.dto.BorrowHistoryRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowHistoryResponse;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowStatistics;
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
    public PageResponse<BorrowHistoryResponse.Info> getBorrowHistory(BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.search(searchCondition, pageable);
        Page<BorrowHistoryResponse.Info> infos = borrowRecords.map(BorrowHistoryResponse.Info::from);

        return PageResponse.from(infos);
    }

    @Transactional
    public PageResponse<BorrowHistoryResponse.InfoByMember> getBorrowHistoryByMember(Long memberId, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByMemberId(memberId, pageable);
        Page<BorrowHistoryResponse.InfoByMember> infos = borrowRecords.map(BorrowHistoryResponse.InfoByMember::from);

        return PageResponse.from(infos);
    }

    @Transactional
    public PageResponse<BorrowHistoryResponse.InfoByBook> getBorrowHistoryByBook(Long bookId, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByBookId(bookId, pageable);
        Page<BorrowHistoryResponse.InfoByBook> infos = borrowRecords.map(BorrowHistoryResponse.InfoByBook::from);

        return PageResponse.from(infos);
    }


    public BorrowStatistics getBorrowStatistics() {
        return borrowRecordRepository.getBorrowStatistics();
    }

    public PageResponse<BorrowHistoryResponse.Info> getOverdueBorrowRecords(BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchOverdueBorrowRecords(searchCondition, pageable);
        Page<BorrowHistoryResponse.Info> infos = borrowRecords.map(BorrowHistoryResponse.Info::from);

        return PageResponse.from(infos);

    }


}
