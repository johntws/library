package com.aeon.library.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookDto {
    private List<BookCopyDto> copies;
    private String isbn;
    private String title;
    private String author;
}
