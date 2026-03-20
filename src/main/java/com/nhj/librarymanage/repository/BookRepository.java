package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.error.ErrorCode;
import com.nhj.librarymanage.error.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findById(long id);

    default Book get(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOOK_NOT_FOND));
    }


}
