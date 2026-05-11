package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.model.dto.ReturnRequest;
import com.nhj.librarymanage.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReturnService {

    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional
    public void returnBook(ReturnRequest.Create create) {
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findAllById(create.bookRecordIds());


        for (BorrowRecord borrowRecord : borrowRecords) {
            /*if (!isBorrowed(bookItem)) {
                throw new NotReturnableException(BookErrorCode.BOOK_NOT_RETURNABLE);
            }*/

            BookCopy bookCopy = borrowRecord.getBookCopy();
            bookCopy.returnBook();
        }





        //eventPublisher.publishEvent(new BookBorrowableEvent(bookItem.getBook().getId()));
    }

}
