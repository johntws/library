package com.aeon.library.dto;

import lombok.Data;

@Data
public class CreateBookRes {
    private Long copyId;
    private String isbn;
    private String title;
    private String author;
    private boolean borrowed;
}
