package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.dto.BorrowRequest;
import com.nhj.librarymanage.domain.entity.BorrowHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowHistoryRepositoryCustom {

    Page<BorrowHistory> findAll(BorrowRequest.SearchConditionDto searchConditionDto, Pageable pageable);

}