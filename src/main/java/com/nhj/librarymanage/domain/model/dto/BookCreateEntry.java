package com.nhj.librarymanage.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookCreateEntry {

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String location;
    private int stockQuantity;
    private String description;
    private String thumbnailUrl;

}
