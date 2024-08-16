package com.aeon.library.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookDto {
    private List<CopyDto> copies;
    private String isbn;
    private String title;
    private String author;
    private Long copyId;
}
