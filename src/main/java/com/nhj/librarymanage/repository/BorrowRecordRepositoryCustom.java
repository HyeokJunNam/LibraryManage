package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.dto.BorrowHistoryRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.domain.model.dto.BorrowStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowRecordRepositoryCustom {

    Page<BorrowRecord> search(BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable);

    Page<BorrowRecord> searchByMemberId(Long memberId, Pageable pageable);

    Page<BorrowRecord> searchByBookId(Long bookId, Pageable pageable);

    BorrowStatistics getBorrowStatistics();

    Page<BorrowRecord> searchOverdueBorrowRecords(BorrowHistoryRequest.SearchCondition searchCondition, Pageable pageable);

}