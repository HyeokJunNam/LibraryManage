package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepositoryCustom {

    Page<Book> findAll(BookRequest.SearchCondition searchCondition, Pageable pageable);

}
