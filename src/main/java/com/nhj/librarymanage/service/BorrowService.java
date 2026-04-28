package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookItem;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.model.dto.BorrowBookEntry;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.NotBorrowableException;
import com.nhj.librarymanage.repository.BookItemRepository;
import com.nhj.librarymanage.repository.BookRepository;
import com.nhj.librarymanage.repository.BorrowRecordRepository;
import com.nhj.librarymanage.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BorrowService {

    private final ApplicationEventPublisher eventPublisher;

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    private static final long BORROW_DAY = 7;


    // TODO LOCK 처리
    @Transactional
    public void borrow(BorrowRequest.Borrow borrow) {
         List<Long> bookIds = borrow.getBorrowBookEntries().stream().map(BorrowBookEntry::bookId).toList();
        List<Book> books = bookRepository.findBorrowableBook(bookIds);

        Map<Long, Long> borrowRequestMap = borrow.getBorrowBookEntries().stream()
                .collect(Collectors.toMap(
                        BorrowBookEntry::bookId,
                        BorrowBookEntry::quantity,
                        Long::sum
                ));

        Member member = memberRepository.getById(borrow.getMemberId());

        for (Book book : books) {
            List<BookItem> bookItems = book.getBookItems();

            long requestQuantity = borrowRequestMap.get(book.getId()); // throw? 잘못된 조회가 있을리는 없지만..?

            // 대여 가능한 책 수
            if (requestQuantity > bookItems.size()) {
                throw new NotBorrowableException(BookErrorCode.BOOK_NOT_BORROWABLE);
            }

            for (int i = 0 ; i < requestQuantity ; i++) {
                BookItem bookItem = bookItems.get(i);
                bookItem.startBorrow(member, BORROW_DAY);
            }

        }
    }

    private boolean isBorrowed(BookItem bookItem) {
        BorrowRecord borrowRecord = bookItem.getBorrowRecord();
        return borrowRecord != null && borrowRecord.getReturnedAt() == null;
    }

    @Transactional
    public void returnBook(BorrowRequest.ReturnBook returnBook) {
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findAllById(returnBook.getBookRecordIds());


        for (BorrowRecord borrowRecord : borrowRecords) {
            /*if (!isBorrowed(bookItem)) {
                throw new NotReturnableException(BookErrorCode.BOOK_NOT_RETURNABLE);
            }*/

            BookItem bookItem = borrowRecord.getBookitem();
            bookItem.returnBook();
        }





        //eventPublisher.publishEvent(new BookBorrowableEvent(bookItem.getBook().getId()));
    }

}
