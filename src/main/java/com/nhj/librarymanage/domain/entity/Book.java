package com.nhj.librarymanage.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private String location;

    private String description;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "book")
    private List<BookItem> bookItems = new ArrayList<>();


    public void changeTitle(String title) {
        this.title = title;
    }

}
