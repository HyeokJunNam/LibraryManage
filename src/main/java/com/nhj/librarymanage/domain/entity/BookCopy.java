package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "book_copy")
public class BookCopy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Setter
    @Enumerated(EnumType.STRING)
    private BorrowStatus borrowStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_condition")
    private BookCopyCondition bookCopyCondition;

    @OneToMany(mappedBy = "bookCopy", cascade = CascadeType.PERSIST)
    private final List<BorrowRecord> borrowRecords = new ArrayList<>();

    @Builder
    public BookCopy(Book book, BorrowStatus borrowStatus, BookCopyCondition bookCopyCondition) {
        this.book = book;
        this.borrowStatus = borrowStatus;
        this.bookCopyCondition = bookCopyCondition;
    }

    public void update(BookCopyCondition bookCopyCondition) {
        this.bookCopyCondition = bookCopyCondition;
    }

    public void borrow(Member member, long borrowDay) {
        LocalDateTime now = LocalDateTime.now();

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .bookCopy(this)
                .member(member)
                .borrowedAt(now)
                .dueAt(now.plusDays(borrowDay))
                .build();

        this.borrowRecords.add(borrowRecord);
        this.borrowStatus = BorrowStatus.BORROWED;
    }

    public boolean isBorrowable() {
        return bookCopyCondition == BookCopyCondition.NORMAL && borrowStatus ==  BorrowStatus.AVAILABLE;
    }

    public boolean isBorrowed() {
        return borrowStatus ==  BorrowStatus.BORROWED;
    }

}
