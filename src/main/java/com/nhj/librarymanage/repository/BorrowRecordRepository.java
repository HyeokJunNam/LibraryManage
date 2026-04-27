package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import com.nhj.librarymanage.error.exception.book.NotReturnableException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long>, BorrowRecordRepositoryCustom {

    default BorrowRecord getById(Long id) {
        return findById(id).orElseThrow(() -> new NotReturnableException(BookErrorCode.BOOK_NOT_RETURNABLE));
    }

}
