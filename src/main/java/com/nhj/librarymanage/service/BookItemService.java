package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.model.dto.BookItemRequest;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookItemService {

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;

    @Transactional
    public void createBookItem(Long bookId, BookItemRequest.Create create) {
        Book book = bookRepository.getById(bookId);

        BookItem bookItem = BookItem.builder()
                .book(book)
                .status(create.getStatus())
                .build();

        bookItemRepository.save(bookItem);
    }



}
