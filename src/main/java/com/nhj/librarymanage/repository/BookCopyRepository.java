package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    Optional<BookCopy> findById(long id);

    default BookCopy getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(BookErrorCode.BOOK_ITEM_NOT_FOUND));
    }

    List<BookCopy> findAllByBookId(long bookId);

}
