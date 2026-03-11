package com.nhj.librarymanage.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "book")
public class BookEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bookCode;

    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "current_borrow_history_id")
    private BorrowHistoryEntity borrowHistoryEntity;


    public void changeName(String name) {
        this.name = name;
    }

    public void changeBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public boolean isBorrowed() {
        return borrowHistoryEntity != null;
    }


    public void startBorrow(MemberEntity memberEntity, long borrowDay) {
        LocalDateTime now = LocalDateTime.now();

        this.borrowHistoryEntity = BorrowHistoryEntity.builder()
                .bookEntity(this)
                .memberEntity(memberEntity)
                .borrowedAt(now)
                .dueAt(now.plusDays(borrowDay))
                .build();
    }

    public void endBorrow() {
        this.borrowHistoryEntity.returnBook();
        this.borrowHistoryEntity = null;
    }


}
