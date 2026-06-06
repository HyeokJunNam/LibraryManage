package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.domain.code.BookCopyCondition;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import com.nhj.librarymanage.domain.code.ReturnStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "condition")
    private BookCopyCondition bookCopyCondition;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private BorrowRecord borrowRecord;

    @Builder
    public BookCopy(Book book, BookCopyCondition bookCopyCondition) {
        this.book = book;
        this.bookCopyCondition = bookCopyCondition;
    }

    public void update(BookCopyCondition bookCopyCondition) {
        this.bookCopyCondition = bookCopyCondition;
    }

    public void startBorrow(Member member, long borrowDay) {
        LocalDateTime now = LocalDateTime.now();

        this.borrowRecord = BorrowRecord.builder()
                .bookCopy(this)
                .member(member)
                .borrowedAt(now)
                .dueAt(now.plusDays(borrowDay))
                .build();
    }

    public void releaseBorrow() {
        this.borrowRecord = null;
    }

    public BorrowStatus getBorrowStatus() {
        if (borrowRecord != null) {
            return BorrowStatus.BORROWED;
        }
        else {
            if (BookCopyCondition.NORMAL.equals(bookCopyCondition)) {
                return BorrowStatus.AVAILABLE;
            }
            else {
                return BorrowStatus.UNAVAILABLE;
            }
        }
    }

    public ReturnStatus getReturnStatus() {
        if (borrowRecord != null) {
            if (borrowRecord.getReturnedAt() != null) {
                return ReturnStatus.RETURNED;
            }
            else {
                LocalDate dueDate = borrowRecord.getDueAt().toLocalDate();
                boolean overdue = dueDate.isBefore(LocalDate.now());

                if (overdue) {
                    return  ReturnStatus.OVERDUE;
                }
            }

            return  ReturnStatus.BORROWED;
        }
        else {
            return null;
        }
    }

}
