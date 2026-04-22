package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.model.dto.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepositoryCustom {

    Page<Book> findAll(BookRequest.SearchCondition searchCondition, Pageable pageable);

    List<Book> findBorrowableBook(List<Long> bookIds);

}
