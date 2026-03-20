package com.nhj.librarymanage.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity(name = "book")
@Table(name = "book")
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String isbn;

    private String title;

    private String author;

    private String publisher;

    @OneToMany(mappedBy = "book")
    private List<BookItem> bookItemList = new ArrayList<>();


    public void changeTitle(String title) {
        this.title = title;
    }


    public void startBorrow(Member member, long borrowDay) {
        LocalDateTime now = LocalDateTime.now();

        /*this.borrowHistoryEntity = BorrowHistoryEntity.builder()
                .bookEntity(this)
                .memberEntity(memberEntity)
                .borrowedAt(now)
                .dueAt(now.plusDays(borrowDay))
                .build();*/
    }

    public void endBorrow() {
        /*this.borrowHistoryEntity.returnBook();
        this.borrowHistoryEntity = null;*/
    }


}
