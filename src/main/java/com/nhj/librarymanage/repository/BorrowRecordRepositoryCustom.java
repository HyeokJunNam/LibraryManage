package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowRecordRepositoryCustom {

    Page<BorrowRecord> search(BorrowRequest.SearchCondition searchCondition, Pageable pageable);

    Page<BorrowRecord> searchByMemberId(Long memberId, Pageable pageable);

}