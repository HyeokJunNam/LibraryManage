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

    private String location;

    @Enumerated(EnumType.STRING)
    private BookItemStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private BorrowRecord borrowRecord;

    @Builder
    public BookItem(Book book, BookItemStatus status, String location) {
        this.book = book;
        this.status = status;
        this.location = location;
    }

    public void update(BookItemStatus status, String location) {
        this.status = status;
        this.location = location;
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
