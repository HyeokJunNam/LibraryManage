package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.domain.code.BookCopyStatus;
import com.nhj.librarymanage.domain.code.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;

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

    private String location;

    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private BorrowRecord borrowRecord;

    @Builder
    public BookCopy(Book book, BookCopyStatus status, String location) {
        this.book = book;
        this.status = status;
        this.location = location;
    }

    public void update(BookCopyStatus status, String location) {
        this.status = status;
        this.location = location;
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

    public void returnBook() {
        this.borrowRecord.returnBook();
        this.borrowRecord = null;
    }

    public BorrowStatus getBorrowStatus() {
        if (status == BookCopyStatus.AVAILABLE) {
            if (borrowRecord == null) {
                return BorrowStatus.AVAILABLE;
            }
            else {
                LocalDate dueDate = borrowRecord.getDueAt().toLocalDate();
                boolean overdue = dueDate.isBefore(LocalDate.now());

                if (overdue) {
                    return BorrowStatus.OVERDUE;
                }
                else {
                    return BorrowStatus.BORROWED; //이건데 여기 절대올수없다는거지.
                }
            }
        }
        else {
            return BorrowStatus.UNAVAILABLE;
        }
    }

}
