package com.nhj.librarymanage.domain.entity;

import com.nhj.librarymanage.domain.code.BookItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "book_item")
public class BookItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    private BookItemStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private BorrowRecord borrowRecord;

    @Builder
    public BookItem(Book book) {
        this.book = book;
        this.status = BookItemStatus.AVAILABLE;
    }

    public void startBorrow(Member member, long borrowDay) {
        LocalDateTime now = LocalDateTime.now();

        this.borrowRecord = BorrowRecord.builder()
                .bookitem(this)
                .member(member)
                .borrowedAt(now)
                .dueAt(now.plusDays(borrowDay))
                .build();
    }

    public void returnBook() {
        this.borrowRecord.returnBook();
        this.borrowRecord = null;
    }

}
