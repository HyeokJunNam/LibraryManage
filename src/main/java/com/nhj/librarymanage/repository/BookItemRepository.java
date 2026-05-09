package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookItemRepository extends JpaRepository<BookItem, Long> {

    Optional<BookItem> findById(long id);

    default BookItem getById(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(BookErrorCode.BOOK_ITEM_NOT_FOUND));
    }

    List<BookItem> findAllByBookId(long bookId);

}
