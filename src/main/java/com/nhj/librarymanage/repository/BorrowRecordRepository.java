package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import com.nhj.librarymanage.error.exception.book.NotReturnableException;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long>, BorrowRecordRepositoryCustom {

    @NonNull
    default BorrowRecord getById(@NonNull Long id) {
        return findById(id).orElseThrow(() -> new NotReturnableException(BookErrorCode.BOOK_NOT_RETURNABLE));
    }

}
