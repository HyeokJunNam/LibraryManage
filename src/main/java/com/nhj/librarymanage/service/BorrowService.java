package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Book;
import com.nhj.librarymanage.domain.entity.BookCopy;
import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.model.dto.BorrowRequest;
import com.nhj.librarymanage.error.code.BookErrorCode;
import com.nhj.librarymanage.error.exception.book.NotBorrowableException;
import com.nhj.librarymanage.repository.BookRepository;
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
    private final MemberRepository memberRepository;

    private static final long BORROW_DAY = 7;


    // TODO LOCK 처리
    @Transactional
    public void borrowBook(BorrowRequest.Create create) {
        List<Long> bookIds = create.items().stream().map(BorrowRequest.Create.Item::bookId).toList();
        List<Book> books = bookRepository.findBorrowableBook(bookIds);

        Map<Long, Long> borrowRequestMap = create.items().stream()
                .collect(Collectors.toMap(
                        BorrowRequest.Create.Item::bookId,
                        BorrowRequest.Create.Item::quantity,
                        Long::sum
                ));

        Member member = memberRepository.getById(create.memberId());

        for (Book book : books) {
            List<BookCopy> bookCopies = book.getBookCopies();

            long requestQuantity = borrowRequestMap.get(book.getId()); // throw? 잘못된 조회가 있을리는 없지만..?

            // 대여 가능한 책 수
            if (requestQuantity > bookCopies.size()) {
                throw new NotBorrowableException(BookErrorCode.BOOK_NOT_BORROWABLE);
            }

            for (int i = 0 ; i < requestQuantity ; i++) {
                BookCopy bookCopy = bookCopies.get(i);
                bookCopy.startBorrow(member, BORROW_DAY);
            }

        }
    }

}
