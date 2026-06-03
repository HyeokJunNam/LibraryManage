package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.dto.admin.book.BookBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.book.BookBorrowResponse;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowResponse;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowRequest;
import com.nhj.librarymanage.domain.dto.admin.member.MemberBorrowResponse;
import com.nhj.librarymanage.domain.dto.member.borrow.MyBorrowResponse;
import com.nhj.librarymanage.domain.entity.BorrowRecord;
import com.nhj.librarymanage.domain.dto.admin.common.PageResponse;
import com.nhj.librarymanage.domain.dto.admin.borrow.BorrowRequest;
import com.nhj.librarymanage.model.vo.BorrowStatistics;
import com.nhj.librarymanage.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;


    // 전체 대출 목록
    public PageResponse<BorrowResponse.ListItem> getBorrows(BorrowRequest.Search search, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.search(search, pageable);
        Page<BorrowResponse.ListItem> listItems = borrowRecords.map(BorrowResponse.ListItem::from);

        return PageResponse.from(listItems);
    }

    // 연체 대출 목록
    public PageResponse<BorrowResponse.OverdueListItem> getOverdueBorrows(BorrowRequest.Search search, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchOverdue(search, pageable);
        Page<BorrowResponse.OverdueListItem> overdueListItems = borrowRecords.map(BorrowResponse.OverdueListItem::from);

        return PageResponse.from(overdueListItems);
    }

    // 회원의 반납 가능 대출 목록
    public PageResponse<BorrowResponse.ReturnableListItem> getReturnableBorrowsByMember(Long memberId, MemberBorrowRequest.Search search, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchReturnableByMemberId(memberId, search, pageable);
        Page<BorrowResponse.ReturnableListItem> returnableListItems = borrowRecords.map(BorrowResponse.ReturnableListItem::from);

        return PageResponse.from(returnableListItems);
    }

    // 회원 기준 대출 목록
    public PageResponse<MemberBorrowResponse.ListItem> getBorrowsByMember(Long memberId, MemberBorrowRequest.Search search, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByMemberId(memberId, search, pageable);
        Page<MemberBorrowResponse.ListItem> listItems = borrowRecords.map(MemberBorrowResponse.ListItem::from);

        return PageResponse.from(listItems);
    }

    // 도서 기준 대출 목록
    public PageResponse<BookBorrowResponse.ListItem> getBorrowsByBook(Long bookId, BookBorrowRequest.Search search, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByBookId(bookId, search, pageable);
        Page<BookBorrowResponse.ListItem> listItems = borrowRecords.map(BookBorrowResponse.ListItem::from);

        return PageResponse.from(listItems);
    }


    //

    // 내 대출 목록인데.. 얘는 미완성 TODO
    public PageResponse<MyBorrowResponse.ListItem> getMyBorrows(Long memberId, MemberBorrowRequest.Search search, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.searchByMemberId(memberId, search, pageable);
        Page<MyBorrowResponse.ListItem> listItems = borrowRecords.map(MyBorrowResponse.ListItem::from);

        return PageResponse.from(listItems);
    }



    public BorrowStatistics getBorrowStatistics() {
        return borrowRecordRepository.getBorrowStatistics();
    }


}
