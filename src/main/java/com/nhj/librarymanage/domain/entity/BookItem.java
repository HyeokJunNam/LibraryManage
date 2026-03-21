package com.nhj.librarymanage.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "book_item")
public class BookItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private String status;

    private String location;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private BorrowHistory borrowHistory;


    public void startBorrow(Member member, long borrowDay) {
        LocalDateTime now = LocalDateTime.now();

        this.borrowHistory = BorrowHistory.builder()
                .bookitem(this)
                .member(member)
                .borrowedAt(now)
                .dueAt(now.plusDays(borrowDay))
                .build();
    }

    public void endBorrow() {
        /*this.borrowHistoryEntity.returnBook();
        this.borrowHistoryEntity = null;*/
    }

}
