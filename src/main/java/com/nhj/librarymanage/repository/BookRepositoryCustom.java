package com.nhj.librarymanage.repository;

import com.nhj.librarymanage.domain.dto.admin.book.BookSearch;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.dto.admin.book.BookManageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepositoryCustom {

    Page<Book> search(BookSearch search, Pageable pageable);

    List<Book> findBorrowableBook(List<Long> bookIds);

}
