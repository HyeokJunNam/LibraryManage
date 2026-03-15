package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.BookRequest;
import com.nhj.librarymanage.domain.dto.BookResponse;
import com.nhj.librarymanage.domain.entity.BookEntity;
import com.nhj.librarymanage.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookManageService {

    private final BookRepository bookRepository;

    public BookResponse.InfoDto getBook(long id) {
        return BookResponse.InfoDto.toDto(bookRepository.get(id));
    }

    @Transactional
    public List<BookResponse.InfoDto> getBooks(BookRequest.ParamDto paramDto) {
        List<BookEntity> bookEntityList = bookRepository.findAll();

        return bookEntityList.stream().map(BookResponse.InfoDto::toDto).toList();
    }


    @Transactional
    public void createBook(BookRequest.CreateDto createDto) {
        BookEntity bookEntity = BookEntity.builder()
                .bookCode(createDto.getBookCode())
                .name(createDto.getName())

                .build();

        bookRepository.save(bookEntity);
    }

    @Transactional
    public void updateBook(BookRequest.UpdateDto updateDto) {
        BookEntity bookEntity = bookRepository.get(updateDto.getId());

        bookEntity.changeName(updateDto.getName());
    }

    @Transactional
    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

}
