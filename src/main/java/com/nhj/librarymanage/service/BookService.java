package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.admin.book.BookSearch;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.member.book.BookResponse;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse.Detail getBook(Long id) {
        return BookResponse.Detail.from(bookRepository.getById(id));
    }

    @Transactional
    public PageResponse<BookResponse.ListItem> getBooks(BookSearch search, Pageable pageable) {
        Page<Book> books = bookRepository.search(search, pageable);
        return PageResponse.from(books.map(BookResponse.ListItem::from));
    }

}
