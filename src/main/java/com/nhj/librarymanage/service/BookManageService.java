package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookManageService {

    private final BookRepository bookRepository;

    public BookResponse.InfoDto getBook(long id) {
        return BookResponse.InfoDto.toDto(bookRepository.get(id));
    }

    @Transactional
    public List<BookResponse.InfoDto> getBooks() {
        List<Book> bookList = bookRepository.findAll();

        return bookList.stream().map(BookResponse.InfoDto::toDto).toList();
    }


    @Transactional
    public void createBook(List<BookRequest.CreateDto> createDtoList) {
        List<Book> bookList = new ArrayList<>();

        for (BookRequest.CreateDto createDto : createDtoList) {
        Book book = Book.builder()
                .isbn(createDto.getIsbn())
                .title(createDto.getTitle())
                .author(createDto.getAuthor())
                .publisher(createDto.getPublisher())
                .build();

            bookList.add(book);
        }

        bookRepository.saveAll(bookList);
    }

    @Transactional
    public void updateBook(BookRequest.UpdateDto updateDto) {
        Book book = bookRepository.get(updateDto.getId());

        book.changeTitle(updateDto.getName());
    }

    @Transactional
    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

}
