package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    Optional<Book> findById(long id);

    default Book get(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(BookErrorCode.BOOK_NOT_FOUND));
    }


}
