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

    private String description;

    private String location;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "book")
    private List<BookCopy> bookCopies = new ArrayList<>();

    public void updateBookInfo(String isbn,
                               String title,
                               String author,
                               String publisher,
                               String description,
                               String thumbnailUrl,
                               String location) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.location = location;
    }

}
