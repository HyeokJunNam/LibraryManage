package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowRecordRepositoryCustom {

    Page<BorrowRecord> search(BorrowRequest.SearchCondition searchCondition, Pageable pageable);

    Page<BorrowRecord> searchByMemberId(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable);

    Page<BorrowRecord> searchReturnableByMemberId(Long memberId, BorrowRequest.SearchConditionByMember searchCondition, Pageable pageable);

    Page<BorrowRecord> searchByBookId(Long bookId, BorrowRequest.SearchConditionByBook searchCondition, Pageable pageable);

    BorrowStatistics getBorrowStatistics();

    Page<BorrowRecord> searchOverdueBorrowRecords(BorrowRequest.SearchCondition searchCondition, Pageable pageable);

}