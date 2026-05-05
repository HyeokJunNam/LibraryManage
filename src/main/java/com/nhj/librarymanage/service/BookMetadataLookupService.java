package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.model.dto.BookLookupResponse;
import com.nhj.librarymanage.domain.model.dto.NlLibraryBookSearchApi;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.BookMetadataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BookMetadataLookupService {

    private final NlLibraryClient nlLibraryClient;

    public BookLookupResponse lookupByIsbn(String isbn) {
        try {
            NlLibraryBookSearchApi.Receive.Doc doc = nlLibraryClient.searchByIsbn(isbn).docs().getFirst();

            return BookLookupResponse.builder()
                    .isbn(doc.eaIsbn())
                    .title(doc.title())
                    .author(doc.author())
                    .publisher(doc.publisher())
                    .description(doc.bookIntroduction())
                    .build();
        }
        catch (NoSuchElementException ex) {
            throw new BookMetadataNotFoundException(BookErrorCode.BOOK_META_DATA_NOT_FOUND);
        }
    }

}
