package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BookItemRequest;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookItemManageService {

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;

    @Transactional
    public void createBookItem(Long bookId, BookItemRequest.Create create) {
        Book book = bookRepository.get(bookId);

        BookItem bookItem = BookItem.builder()
                .book(book)
                .status(create.getStatus())
                .location(create.getLocation())
                .build();

        bookItemRepository.save(bookItem);
    }



}
