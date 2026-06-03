package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.dto.admin.book.BookBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowRequest;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowRecordRepositoryCustom {

    Page<BorrowRecord> search(BorrowRequest.Search search, Pageable pageable);

    Page<BorrowRecord> searchByMemberId(Long memberId, MemberBorrowRequest.Search search, Pageable pageable);

    Page<BorrowRecord> searchByBookId(Long bookId, BookBorrowRequest.Search search, Pageable pageable);

    Page<BorrowRecord> searchReturnableByMemberId(Long memberId, MemberBorrowRequest.Search search, Pageable pageable);

    BorrowStatistics getBorrowStatistics();

    Page<BorrowRecord> searchOverdue(BorrowRequest.Search search, Pageable pageable);

}